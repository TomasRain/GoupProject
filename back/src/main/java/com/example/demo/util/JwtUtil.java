package com.example.demo.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final long EXPIRATION_TIME = 60*5*1000; // 5分钟，单位为毫秒
    private static final String SECRET_KEY = "YourSecretKeyForJWTGenerationReplaceThisWithAStrongKey"; // 请替换为您的密钥

    private final Key key;

    public JwtUtil() {
        // 使用 SECRET_KEY 生成签名密钥
        key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // 生成令牌
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // 设置主题，即用户名
                .setIssuedAt(new Date()) // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 过期时间
                .signWith(key, SignatureAlgorithm.HS256) // 签名算法和密钥
                .compact();
    }

    // 从令牌中获取用户名
    public String extractUsername(String token) {
        Claims claims = extractClaims(token);
        return claims != null ? claims.getSubject() : null;
    }

    // 验证令牌
    public boolean validateToken(String token, String username) {
        String extractedUsername = extractUsername(token);
        return (extractedUsername != null && extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // 检查令牌是否过期
    private boolean isTokenExpired(String token) {
        Claims claims = extractClaims(token);
        return claims != null && claims.getExpiration().before(new Date());
    }

    // 提取所有声明（Claims）
    private Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key) // 设置签名密钥
                    .build()
                    .parseClaimsJws(token) // 解析令牌
                    .getBody();
        } catch (JwtException e) {
            // 令牌无效
            return null;
        }
    }
}
