-- ==============================
-- V2__create_core_tables.sql
-- 核心业务表：stores, products, orders, order_items
-- ==============================

-- 1. 门店表 (stores)
CREATE TABLE stores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    org_id BIGINT NOT NULL COMMENT '集团/商户ID',
    store_id BIGINT NOT NULL COMMENT '门店ID（对外唯一编码，可与 id 一致或独立）',

    name VARCHAR(100) NOT NULL COMMENT '门店名称',
    address VARCHAR(255) DEFAULT NULL COMMENT '门店地址',
    phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    status ENUM('ACTIVE','INACTIVE') DEFAULT 'ACTIVE' COMMENT '门店状态',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    is_deleted TINYINT(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2. 商品表 (products)
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    org_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,

    name VARCHAR(100) NOT NULL COMMENT '商品名称',
    description VARCHAR(255) DEFAULT NULL COMMENT '商品描述',
    price DECIMAL(12,2) NOT NULL COMMENT '价格',
    currency VARCHAR(10) NOT NULL DEFAULT 'USD' COMMENT '币种',
    stock INT DEFAULT 0 COMMENT '库存数量',
    status ENUM('ACTIVE','INACTIVE') DEFAULT 'ACTIVE' COMMENT '商品状态',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    is_deleted TINYINT(1) DEFAULT 0,

    INDEX idx_products_store (org_id, store_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3. 订单表 (orders)
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    org_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,

    user_id BIGINT NOT NULL COMMENT '下单用户',
    total_amount DECIMAL(12,2) NOT NULL COMMENT '订单总金额',
    currency VARCHAR(10) NOT NULL DEFAULT 'USD' COMMENT '币种',
    status ENUM('CREATED','PAID','CANCELLED','COMPLETED') NOT NULL DEFAULT 'CREATED' COMMENT '订单状态',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    is_deleted TINYINT(1) DEFAULT 0,

    INDEX idx_orders_store (org_id, store_id),
    INDEX idx_orders_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 4. 订单明细表 (order_items)
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    org_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,

    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '数量',
    price DECIMAL(12,2) NOT NULL COMMENT '单价',
    currency VARCHAR(10) NOT NULL DEFAULT 'USD' COMMENT '币种',
    subtotal DECIMAL(12,2) NOT NULL COMMENT '小计金额',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    is_deleted TINYINT(1) DEFAULT 0,

    INDEX idx_order_items_order (order_id),
    INDEX idx_order_items_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
