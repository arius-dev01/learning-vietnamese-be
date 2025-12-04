package com.example.vietjapaneselearning.security;


import com.example.vietjapaneselearning.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private int expiration;

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().getName());
        claims.put("fullName", user.getFullName());
        return Jwts.builder()
                .setSubject(user.getEmail())

                .claim("role", user.getRole().getName())
                .claim("fullName", user.getFullName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signingKey(secret), SignatureAlgorithm.HS256)
                .compact();
    }

    public Key signingKey(String secret) {
        byte[] bytes = secret.getBytes();
        return Keys.hmacShaKeyFor(bytes);
    }

    public Date isExpiration(String token) {
        return extractToken(token, Claims::getExpiration);
    }

    public String extractEmail(String token) {
        return extractToken(token, Claims::getSubject);
    }

    public boolean validate(String token, UserDetails userDetails) {
        String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && isExpiration(token).after(new Date()));
    }

    private Claims extractAllToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey(secret))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T extractToken(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = extractAllToken(token);
        return claimsTFunction.apply(claims);
    }
}
