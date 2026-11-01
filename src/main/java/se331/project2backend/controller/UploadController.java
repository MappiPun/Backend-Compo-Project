package se331.project2backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import se331.project2backend.dto.UploadResponse;
import se331.project2backend.service.StorageService;

@RestController
@RequiredArgsConstructor
public class UploadController {

    private final StorageService storageService;

    /** Uploads an image (multipart field "file") to the cloud storage and returns its url. */
    @PostMapping("/uploadImage")
    public UploadResponse upload(@RequestParam("file") MultipartFile file) {
        return new UploadResponse(storageService.uploadImage(file));
    }
}
