package com.example.pos_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 * 继承 OncePerRequestFilter，确保每个请求只执行一次
 * 从请求头中提取 JWT 令牌并进行验证
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProvider jwtProvider;

    // TODO: 注入 UserDetailsService 实现
    // @Autowired
    // private UserDetailsService userDetailsService;

    /**
     * 执行过滤逻辑
     *
     * @param request     HTTP 请求
     * @param response    HTTP 响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // 从请求头中获取 JWT 令牌
            String jwt = getJwtFromRequest(request);
            log.debug("提取到的JWT令牌: {}", jwt != null ? jwt.substring(0, Math.min(jwt.length(), 20)) + "..." : "null");
            
            if (StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)) {
                log.debug("JWT令牌验证成功");
                // 从令牌中获取用户名
                String username = jwtProvider.getUsernameFromToken(jwt);
                
                // TODO: 从数据库加载用户详情
                // UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // 临时实现：创建一个简单的认证对象
                // 生产环境中应该从数据库加载完整的用户信息
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // TODO: 替换为实际的用户详情加载逻辑
                    /*
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    if (jwtProvider.validateToken(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                userDetails, 
                                null, 
                                userDetails.getAuthorities()
                            );
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                    */
                    
                    // 临时实现：直接设置认证信息
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(username, null, null);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    log.debug("已为用户 '{}' 设置安全上下文", username);
                }
            } else {
                log.debug("JWT令牌无效或为空，jwt: {}", jwt != null ? "存在但无效" : "null");
            }
        } catch (Exception ex) {
            log.error("无法设置用户认证信息", ex);
        }
        
        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中提取 JWT 令牌
     *
     * @param request HTTP 请求
     * @return JWT 令牌，如果不存在则返回 null
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        // 从 Authorization 头部获取令牌
        String bearerToken = request.getHeader(SecurityConstants.JWT.HEADER_STRING);
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(SecurityConstants.JWT.TOKEN_PREFIX)) {
            // 移除 "Bearer " 前缀
            return bearerToken.substring(SecurityConstants.JWT.TOKEN_PREFIX.length());
        }
        
        // 也可以从查询参数中获取令牌（用于 WebSocket 等场景）
        String tokenParam = request.getParameter("token");
        if (StringUtils.hasText(tokenParam)) {
            return tokenParam;
        }
        
        return null;
    }

    /**
     * 判断是否应该跳过此过滤器
     * 可以在这里添加不需要认证的路径
     *
     * @param request HTTP 请求
     * @return 是否跳过
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // 跳过认证相关的端点
        return path.startsWith("/api/auth/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs/") ||
               path.startsWith("/swagger-resources/") ||
               path.startsWith("/webjars/") ||
               path.equals("/favicon.ico") ||
               path.equals("/error") ||
               path.startsWith("/actuator/health");
    }

    /**
     * 设置 JWT 提供者
     * 用于测试或依赖注入失败时的手动设置
     *
     * @param jwtProvider JWT 提供者
     */
    public void setJwtProvider(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    /**
     * 设置用户详情服务
     * 用于测试或依赖注入失败时的手动设置
     *
     * @param userDetailsService 用户详情服务
     */
    // TODO: 取消注释并实现
    /*
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    */

    /**
     * 从请求中获取客户端 IP 地址
     * 可用于日志记录和安全审计
     *
     * @param request HTTP 请求
     * @return 客户端 IP 地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * 记录认证失败的详细信息
     *
     * @param request   HTTP 请求
     * @param exception 异常信息
     */
    private void logAuthenticationFailure(HttpServletRequest request, Exception exception) {
        String clientIp = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String requestUri = request.getRequestURI();
        
        log.warn("JWT 认证失败 - IP: {}, URI: {}, User-Agent: {}, 错误: {}", 
                clientIp, requestUri, userAgent, exception.getMessage());
    }
}
