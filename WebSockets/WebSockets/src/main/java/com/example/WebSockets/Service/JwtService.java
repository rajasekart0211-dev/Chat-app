package com.example.WebSockets.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final String secret =
            "my-super-secret-key-for-chat-application-123456789";

    private final long expiration =
            1000 * 60 * 60; // 1 hour

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateToken(String phoneNumber) {

        Date now = new Date();

        Date expiry = new Date(
                now.getTime() + expiration
        );

        return Jwts.builder()
                .subject(phoneNumber)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    public String extractPhoneNumber(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}