CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    org_id BIGINT NOT NULL COMMENT '集团/商户ID',
    store_id BIGINT NOT NULL COMMENT '门店ID',

    -- 登录与身份
    username VARCHAR(50) NOT NULL COMMENT '用户名（唯一）',
    email VARCHAR(100) NOT NULL COMMENT '邮箱（唯一）',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密后存储）',

    -- 用户角色/状态
    role ENUM('OWNER', 'MANAGER', 'STAFF') NOT NULL DEFAULT 'STAFF' COMMENT '角色',
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '账号状态',

    -- 审计字段
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 索引
CREATE UNIQUE INDEX idx_users_username ON users (org_id, username);
CREATE UNIQUE INDEX idx_users_email ON users (org_id, email);
CREATE INDEX idx_users_store ON users (store_id);
