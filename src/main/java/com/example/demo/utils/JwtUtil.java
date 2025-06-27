package com.example.demo.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtUtil {

    @Value("${jwt.secret:mySecretKey123456789abcdefghijklmnopqrstuvwxyz}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24小时
    private Long expiration;

    // 简单的token黑名单存储（生产环境建议使用Redis）
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成JWT token
     */
    public String generateToken(String userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从token中获取用户ID
     */
    public String getUserIdFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * 从token中获取角色
     */
    public String getRoleFromToken(String token) {
        return (String) getClaimsFromToken(token).get("role");
    }

    /**
     * 获取token的过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    /**
     * 从token中获取claims
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 判断token是否过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 验证token
     */
    public Boolean validateToken(String token) {
        try {
            // 检查token是否在黑名单中
            if (blacklistedTokens.contains(token)) {
                return false;
            }

            // 解析token
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 使token失效（加入黑名单）
     */
    public void invalidateToken(String token) {
        blacklistedTokens.add(token);
    }

    /**
     * 刷新token（生成新的token）
     */
    public String refreshToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            String userId = claims.getSubject();
            String role = (String) claims.get("role");

            // 使旧token失效
            invalidateToken(token);

            // 生成新token
            return generateToken(userId, role);
        } catch (Exception e) {
            throw new RuntimeException("Token刷新失败");
        }
    }
}