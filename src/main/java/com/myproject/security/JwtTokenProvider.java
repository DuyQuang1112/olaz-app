package com.myproject.security;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;


@Component
public class JwtTokenProvider {

    // for local
//    private static final Dotenv dotenv = Dotenv.load();
//    private final String SIGNER_KEY = dotenv.get("API_SIGNER_KEY");

    @Value("${api_signer_key}")
    private String SIGNER_KEY;

    public String generateToken(CustomUserDetails customUserDetails) {
        SecretKey key = Keys.hmacShaKeyFor(SIGNER_KEY.getBytes());
        return Jwts.builder()
                .setSubject(customUserDetails.getUsername())
                .claim("userId",customUserDetails.getUser().getId())
                .claim("role",customUserDetails.getRoleName())
                .setId(UUID.randomUUID().toString())
                .setIssuer("QuangDuy")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
}
