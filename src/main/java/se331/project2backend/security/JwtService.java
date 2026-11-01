package se331.project2backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    /** Base64 encoded secret of at least 256 bits (application.security.jwt.secret-key) */
    private final SecretKey key;
    private final long expirationMs;

    public JwtService(@Value("${application.security.jwt.secret-key}") String secretKey,
                      @Value("${application.security.jwt.expiration:86400000}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username, String role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    /** @return the username of a valid token, empty when the token is invalid or expired */
    public Optional<String> extractUsername(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            return Optional.ofNullable(claims.getSubject());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
