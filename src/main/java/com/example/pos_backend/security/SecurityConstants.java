package com.example.pos_backend.security;

/**
 * 安全相关常量定义
 * 包含 JWT 和认证相关的常量
 */
public class SecurityConstants {

    /**
     * JWT 相关常量
     */
    public static class JWT {
        /**
         * JWT 密钥（生产环境应从配置文件或环境变量读取）
         * 至少需要512位（64字符）用于HMAC-SHA512
         */
        public static final String SECRET = "pos-system-jwt-secret-key-2024-this-is-a-very-long-secure-key-for-hmac-sha512-algorithm-minimum-64-characters";
        
        /**
         * JWT 过期时间（毫秒）- 24小时
         */
        public static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000L;
        
        /**
         * JWT 刷新令牌过期时间（毫秒）- 7天
         */
        public static final long REFRESH_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000L;
        
        /**
         * JWT 令牌前缀
         */
        public static final String TOKEN_PREFIX = "Bearer ";
        
        /**
         * JWT 请求头名称
         */
        public static final String HEADER_STRING = "Authorization";
        
        /**
         * JWT 签发者
         */
        public static final String ISSUER = "pos-system";
        
        /**
         * JWT 受众
         */
        public static final String AUDIENCE = "pos-users";
    }

    /**
     * 认证相关常量
     */
    public static class Auth {
        /**
         * 登录URL
         */
        public static final String LOGIN_URL = "/api/auth/login";
        
        /**
         * 注册URL
         */
        public static final String REGISTER_URL = "/api/auth/register";
        
        /**
         * 刷新令牌URL
         */
        public static final String REFRESH_URL = "/api/auth/refresh";
        
        /**
         * 登出URL
         */
        public static final String LOGOUT_URL = "/api/auth/logout";
        
        /**
         * 用户信息URL
         */
        public static final String USER_INFO_URL = "/api/auth/me";
        
        /**
         * 密码重置URL
         */
        public static final String RESET_PASSWORD_URL = "/api/auth/reset-password";
        
        /**
         * 修改密码URL
         */
        public static final String CHANGE_PASSWORD_URL = "/api/auth/change-password";
    }

    /**
     * 权限相关常量
     */
    public static class Permission {
        /**
         * 管理员权限
         */
        public static final String ADMIN = "ROLE_ADMIN";
        
        /**
         * 经理权限
         */
        public static final String MANAGER = "ROLE_MANAGER";
        
        /**
         * 员工权限
         */
        public static final String STAFF = "ROLE_STAFF";
        
        /**
         * 店主权限
         */
        public static final String OWNER = "ROLE_OWNER";
        
        /**
         * 用户权限前缀
         */
        public static final String ROLE_PREFIX = "ROLE_";
    }

    /**
     * 会话相关常量
     */
    public static class Session {
        /**
         * 用户ID在会话中的键名
         */
        public static final String USER_ID_KEY = "userId";
        
        /**
         * 用户名在会话中的键名
         */
        public static final String USERNAME_KEY = "username";
        
        /**
         * 用户角色在会话中的键名
         */
        public static final String USER_ROLE_KEY = "userRole";
        
        /**
         * 组织ID在会话中的键名
         */
        public static final String ORG_ID_KEY = "orgId";
        
        /**
         * 门店ID在会话中的键名
         */
        public static final String STORE_ID_KEY = "storeId";
        
        /**
         * 登录时间在会话中的键名
         */
        public static final String LOGIN_TIME_KEY = "loginTime";
        
        /**
         * 最后活动时间在会话中的键名
         */
        public static final String LAST_ACTIVITY_KEY = "lastActivity";
    }

    /**
     * 安全配置常量
     */
    public static class Security {
        /**
         * 密码最小长度
         */
        public static final int PASSWORD_MIN_LENGTH = 8;
        
        /**
         * 密码最大长度
         */
        public static final int PASSWORD_MAX_LENGTH = 128;
        
        /**
         * 登录失败最大尝试次数
         */
        public static final int MAX_LOGIN_ATTEMPTS = 5;
        
        /**
         * 账户锁定时间（分钟）
         */
        public static final int ACCOUNT_LOCK_DURATION_MINUTES = 30;
        
        /**
         * 会话超时时间（分钟）
         */
        public static final int SESSION_TIMEOUT_MINUTES = 30;
        
        /**
         * 记住我功能的有效期（天）
         */
        public static final int REMEMBER_ME_VALIDITY_DAYS = 30;
    }

    /**
     * 加密相关常量
     */
    public static class Encryption {
        /**
         * BCrypt 加密强度
         */
        public static final int BCRYPT_STRENGTH = 12;
        
        /**
         * AES 加密算法
         */
        public static final String AES_ALGORITHM = "AES";
        
        /**
         * AES 加密模式
         */
        public static final String AES_TRANSFORMATION = "AES/CBC/PKCS5Padding";
        
        /**
         * RSA 加密算法
         */
        public static final String RSA_ALGORITHM = "RSA";
        
        /**
         * RSA 密钥长度
         */
        public static final int RSA_KEY_LENGTH = 2048;
    }

    /**
     * API 安全常量
     */
    public static class ApiSecurity {
        /**
         * API 密钥请求头名称
         */
        public static final String API_KEY_HEADER = "X-API-Key";
        
        /**
         * 客户端ID请求头名称
         */
        public static final String CLIENT_ID_HEADER = "X-Client-Id";
        
        /**
         * 时间戳请求头名称
         */
        public static final String TIMESTAMP_HEADER = "X-Timestamp";
        
        /**
         * 签名请求头名称
         */
        public static final String SIGNATURE_HEADER = "X-Signature";
        
        /**
         * 随机数请求头名称
         */
        public static final String NONCE_HEADER = "X-Nonce";
        
        /**
         * 请求有效期（秒）
         */
        public static final long REQUEST_VALIDITY_SECONDS = 300;
    }

    /**
     * 私有构造函数，防止实例化
     */
    private SecurityConstants() {
        throw new IllegalStateException("Utility class");
    }
}
