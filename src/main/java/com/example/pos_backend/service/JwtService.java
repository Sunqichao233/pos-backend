package com.example.pos_backend.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * JWT令牌服务类
 * 处理Access Token和Refresh Token的生成、验证等
 */
@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret:mySecretKey123456789012345678901234567890}")
    private String jwtSecret;

    @Value("${jwt.access-token-expiration:3600}") // 1小时
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration:2592000}") // 30天
    private Long refreshTokenExpiration;

    /**
     * 生成Access Token
     */
    public String generateAccessToken(String merchantId, String email, String businessName) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("merchant_id", merchantId);
        claims.put("email", email);
        claims.put("business_name", businessName);
        claims.put("token_type", "access");

        return createToken(claims, merchantId, accessTokenExpiration);
    }

    /**
     * 生成Refresh Token
     */
    public String generateRefreshToken(String merchantId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("merchant_id", merchantId);
        claims.put("token_type", "refresh");
        claims.put("jti", UUID.randomUUID().toString()); // JWT ID for refresh token

        return createToken(claims, merchantId, refreshTokenExpiration);
    }

    /**
     * 创建JWT令牌
     */
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        Instant now = Instant.now();
        Instant expiryDate = now.plusSeconds(expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从令牌中提取商家ID
     */
    public String getMerchantIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * 从令牌中提取邮箱
     */
    public String getEmailFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("email", String.class);
    }

    /**
     * 从令牌中提取令牌类型
     */
    public String getTokenTypeFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("token_type", String.class);
    }

    /**
     * 获取令牌过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 验证令牌是否有效
     */
    public boolean isTokenValid(String token) {
        try {
            getClaimsFromToken(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 验证令牌是否过期
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 验证Refresh Token
     */
    public boolean isRefreshToken(String token) {
        try {
            String tokenType = getTokenTypeFromToken(token);
            return "refresh".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证Access Token
     */
    public boolean isAccessToken(String token) {
        try {
            String tokenType = getTokenTypeFromToken(token);
            return "access".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从令牌中获取Claims
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成会话ID
     */
    public String generateSessionId() {
        return "SES-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 获取Access Token过期时间（秒）
     */
    public Long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    /**
     * 获取Refresh Token过期时间（秒）
     */
    public Long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}
