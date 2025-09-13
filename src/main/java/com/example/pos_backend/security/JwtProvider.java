package com.example.pos_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT 令牌提供者
 * 负责 JWT 令牌的生成、验证和解析
 */
@Slf4j
@Component
public class JwtProvider {

    @Value("${jwt.secret:" + SecurityConstants.JWT.SECRET + "}")
    private String jwtSecret;

    @Value("${jwt.expiration:" + SecurityConstants.JWT.EXPIRATION_TIME + "}")
    private long jwtExpirationTime;

    @Value("${jwt.refresh-expiration:" + SecurityConstants.JWT.REFRESH_EXPIRATION_TIME + "}")
    private long refreshExpirationTime;

    /**
     * 获取签名密钥
     *
     * @return 签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 从认证信息生成 JWT 令牌
     *
     * @param authentication 认证信息
     * @return JWT 令牌
     */
    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return generateTokenFromUsername(userPrincipal.getUsername());
    }

    /**
     * 从用户名生成 JWT 令牌
     *
     * @param username 用户名
     * @return JWT 令牌
     */
    public String generateTokenFromUsername(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, jwtExpirationTime);
    }

    /**
     * 从用户详情生成 JWT 令牌
     *
     * @param userDetails 用户详情
     * @return JWT 令牌
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // TODO: 可以在这里添加自定义声明
        // claims.put("userId", user.getId());
        // claims.put("orgId", user.getOrgId());
        // claims.put("role", user.getRole());
        return createToken(claims, userDetails.getUsername(), jwtExpirationTime);
    }

    /**
     * 生成刷新令牌
     *
     * @param username 用户名
     * @return 刷新令牌
     */
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createToken(claims, username, refreshExpirationTime);
    }

    /**
     * 创建 JWT 令牌
     *
     * @param claims     声明
     * @param subject    主题（通常是用户名）
     * @param expiration 过期时间（毫秒）
     * @return JWT 令牌
     */
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(SecurityConstants.JWT.ISSUER)
                .setAudience(SecurityConstants.JWT.AUDIENCE)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token JWT 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从令牌中获取过期时间
     *
     * @param token JWT 令牌
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 从令牌中获取签发时间
     *
     * @param token JWT 令牌
     * @return 签发时间
     */
    public Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    /**
     * 从令牌中获取指定声明
     *
     * @param token          JWT 令牌
     * @param claimsResolver 声明解析器
     * @param <T>            声明类型
     * @return 声明值
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从令牌中获取所有声明
     *
     * @param token JWT 令牌
     * @return 所有声明
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 检查令牌是否过期
     *
     * @param token JWT 令牌
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            log.warn("检查令牌过期状态时发生异常: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 验证令牌
     *
     * @param token       JWT 令牌
     * @param userDetails 用户详情
     * @return 是否有效
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            log.warn("验证令牌时发生异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 验证令牌（仅验证令牌本身）
     *
     * @param token JWT 令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("令牌验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 刷新令牌
     *
     * @param token 原始令牌
     * @return 新的令牌
     */
    public String refreshToken(String token) {
        try {
            final Claims claims = getAllClaimsFromToken(token);
            String username = claims.getSubject();
            
            // 重新创建令牌而不是修改现有 claims
            Map<String, Object> newClaims = new HashMap<>(claims);
            return createToken(newClaims, username, jwtExpirationTime);
        } catch (Exception e) {
            log.error("刷新令牌时发生异常: {}", e.getMessage());
            throw new JwtException("无法刷新令牌", e);
        }
    }

    /**
     * 从令牌中获取自定义声明
     *
     * @param token JWT 令牌
     * @param key   声明键
     * @return 声明值
     */
    public Object getCustomClaimFromToken(String token, String key) {
        try {
            final Claims claims = getAllClaimsFromToken(token);
            return claims.get(key);
        } catch (Exception e) {
            log.warn("获取自定义声明时发生异常: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 检查令牌是否可以刷新
     *
     * @param token JWT 令牌
     * @return 是否可以刷新
     */
    public boolean canTokenBeRefreshed(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.warn("检查令牌是否可刷新时发生异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取令牌剩余有效时间（秒）
     *
     * @param token JWT 令牌
     * @return 剩余有效时间（秒）
     */
    public long getTokenRemainingTime(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            long remainingTime = (expiration.getTime() - System.currentTimeMillis()) / 1000;
            return Math.max(0, remainingTime);
        } catch (Exception e) {
            log.warn("获取令牌剩余时间时发生异常: {}", e.getMessage());
            return 0;
        }
    }
}
