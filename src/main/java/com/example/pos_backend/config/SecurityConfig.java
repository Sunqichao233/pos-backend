package com.example.pos_backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Spring Security 安全配置
 * 配置认证授权规则，支持 JWT 认证
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    // TODO: 注入 JwtAuthenticationFilter
    // @Autowired
    // private JwtAuthenticationFilter jwtAuthenticationFilter;

    // TODO: 注入 JwtAuthenticationEntryPoint
    // @Autowired
    // private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 安全过滤器链配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF（使用 JWT 时通常禁用）
                .csrf(AbstractHttpConfigurer::disable)
                
                // 配置 CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                
                // 配置会话管理（无状态）
                .sessionManagement(session -> 
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // 配置异常处理
                .exceptionHandling(exceptions -> {
                    // TODO: 配置认证入口点
                    // exceptions.authenticationEntryPoint(jwtAuthenticationEntryPoint);
                })
                
                // 配置授权规则
                .authorizeHttpRequests(auth -> auth
                        // 公开访问的端点
                        .requestMatchers(
                                "/api/auth/**",           // 认证相关接口（登录、刷新token等）
                                "/api/users/register",    // 用户注册接口
                                "/api/public/**",         // 公开接口
                                "/swagger-ui/**",         // Swagger UI
                                "/v3/api-docs/**",        // OpenAPI 文档
                                "/swagger-resources/**",   // Swagger 资源
                                "/webjars/**",            // Web 资源
                                "/favicon.ico",           // 图标
                                "/error",                 // 错误页面
                                "/actuator/health"        // 健康检查
                        ).permitAll()
                        
                        // 用户管理接口（需要认证）
                        .requestMatchers("/api/users/**").authenticated()
                        
                        // 订单管理接口（需要认证）
                        .requestMatchers("/api/orders/**").authenticated()
                        
                        // 商品管理接口（需要认证）
                        .requestMatchers("/api/products/**").authenticated()
                        
                        // 系统管理接口（需要管理员权限）
                        .requestMatchers("/api/system/**").hasRole("ADMIN")
                        
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                );

        // TODO: 添加 JWT 认证过滤器
        // http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 开发环境安全配置（可选）
     * 在开发环境中可以使用此配置来禁用安全认证
     */
    // @Profile("dev")
    // @Bean
    // public SecurityFilterChain devFilterChain(HttpSecurity http) throws Exception {
    //     return http
    //             .csrf(AbstractHttpConfigurer::disable)
    //             .cors(cors -> cors.configurationSource(corsConfigurationSource))
    //             .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
    //             .build();
    // }
}
