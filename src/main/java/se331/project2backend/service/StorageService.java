package se331.project2backend.service;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

/**
 * Uploads the images (the profile images) to the Supabase Storage bucket through its S3 compatible API
 * and returns the public url, which is saved together with the user.
 */
@Service
public class StorageService {

    private static final long MAX_SIZE = 5 * 1024 * 1024;
    private static final Map<String, String> EXTENSIONS = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/gif", "gif",
            "image/webp", "webp");

    private final String endpoint;
    private final String bucket;
    private final String region;
    private final String accessKey;
    private final String secretKey;
    private final String endpointOutput;

    private S3Client client;

    public StorageService(@Value("${supabase.storage.endpoint:}") String endpoint,
                          @Value("${supabase.storage.bucket:}") String bucket,
                          @Value("${supabase.storage.region:}") String region,
                          @Value("${supabase.storage.accessKey:}") String accessKey,
                          @Value("${supabase.storage.secretKey:}") String secretKey,
                          @Value("${supabase.storage.endpoint_output:}") String endpointOutput) {
        this.endpoint = endpoint;
        this.bucket = bucket;
        this.region = region;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.endpointOutput = endpointOutput;
    }

    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please select an image");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The image must be smaller than 5 MB");
        }
        String extension = EXTENSIONS.get(file.getContentType());
        if (extension == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only JPG, PNG, GIF or WEBP images are allowed");
        }
        if (endpoint.isBlank() || bucket.isBlank() || accessKey.isBlank() || secretKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "The image storage is not configured on the server");
        }

        String key = "profile/" + UUID.randomUUID() + "." + extension;
        try {
            client().putObject(
                    PutObjectRequest.builder().bucket(bucket).key(key).contentType(file.getContentType()).build(),
                    RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot read the image");
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Cannot upload the image to the storage");
        }
        return publicUrl(key);
    }

    /**
     * The public url of the object. Set supabase.storage.endpoint_output to
     * https://[ref].supabase.co/storage/v1/object/public/[bucket] or let it be derived from the S3 endpoint.
     */
    private String publicUrl(String key) {
        if (!endpointOutput.isBlank()) {
            return endpointOutput.replaceAll("/+$", "") + "/" + key;
        }
        String base = endpoint.replaceAll("/+$", "")
                .replace(".storage.supabase.co/storage/v1/s3", ".supabase.co/storage/v1/object/public/" + bucket);
        return base + "/" + key;
    }

    private synchronized S3Client client() {
        if (client == null) {
            client = S3Client.builder()
                    .endpointOverride(URI.create(endpoint))
                    .region(Region.of(region.isBlank() ? "us-east-1" : region))
                    .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                    .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                    .build();
        }
        return client;
    }

    @PreDestroy
    void close() {
        if (client != null) {
            client.close();
        }
    }
}
