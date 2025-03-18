package com.example.project_management_backend.config;

import com.example.project_management_backend.Model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class JwtUtil {

    private Key secretKeyDecoded;
    private final Set<String> invalidatedTokens = new HashSet<>(); 
    private static final long EXPIRATION_TIME = 86400000;
    @Value("${jwt.secret}") 
    private String secretKey;

    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.length() < 32) {
            throw new IllegalArgumentException("JWT Secret Key must be at least 32 bytes long");
        }
        this.secretKeyDecoded = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }


    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("authorities", List.of("ROLE_" + user.getRole().getRoleName()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKeyDecoded, SignatureAlgorithm.HS256)
                .compact();
    }

  
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKeyDecoded)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

 
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKeyDecoded)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public Boolean validateToken(String token, String email) {
        final String extractedEmail = extractUsername(token);
        return (extractedEmail.equals(email) && !isTokenExpired(token) && !isTokenInvalid(token));
    }

    private Boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKeyDecoded)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    public void invalidateToken(String token) {
        System.out.println("Invalidating token: " + token); 
        invalidatedTokens.add(token);
    }

   
    public boolean isTokenInvalid(String token) {
        boolean isInvalid = invalidatedTokens.contains(token);
        System.out.println("Checking token invalidation: " + token + " -> " + isInvalid); 
        return isInvalid;
    }
}
