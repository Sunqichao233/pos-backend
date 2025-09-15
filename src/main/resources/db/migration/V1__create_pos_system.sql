-- ==============================
-- V1__create_pos_system.sql
-- POS系统完整数据库设计 - Monty风格
-- 包含所有表结构、初始数据、优化和性能调优
-- ==============================

-- 设置默认存储引擎和字符集
SET default_storage_engine = InnoDB;
SET NAMES utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- =================================
-- 1. 商家与门店管理模块（Square风格）
-- =================================

-- 1.1 商家表 (merchants) - Square风格商家注册，使用UUID主键
CREATE TABLE merchants (
    id CHAR(36) NOT NULL PRIMARY KEY COMMENT '商家ID（UUID格式，如MRC-xxx）',
    email VARCHAR(255) NOT NULL UNIQUE COMMENT '商家邮箱',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
    business_name VARCHAR(255) NOT NULL COMMENT '企业名称',
    industry VARCHAR(100) NOT NULL COMMENT '行业类型',
    currency CHAR(3) NOT NULL DEFAULT 'USD' COMMENT '币种',
    country CHAR(2) NOT NULL DEFAULT 'US' COMMENT '国家代码',
    status VARCHAR(50) DEFAULT 'ACTIVE' COMMENT '商家状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by CHAR(36) COMMENT '创建人UUID',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by CHAR(36) COMMENT '更新人UUID',
    is_deleted BOOLEAN DEFAULT FALSE,
    
    INDEX idx_merchants_email (email),
    INDEX idx_merchants_status (status, is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商家表（Square风格，UUID主键）';

-- 1.2 商家银行账户表 (merchant_bank_accounts) - 独立管理银行账户
CREATE TABLE merchant_bank_accounts (
    id CHAR(36) NOT NULL PRIMARY KEY COMMENT '银行账户ID（UUID格式）',
    merchant_id CHAR(36) NOT NULL COMMENT '所属商家ID',
    account_number VARCHAR(64) NOT NULL COMMENT '银行账户号',
    routing_number VARCHAR(64) NOT NULL COMMENT '银行路由号',
    account_holder VARCHAR(255) NOT NULL COMMENT '银行账户持有人',
    account_type VARCHAR(50) DEFAULT 'CHECKING' COMMENT '账户类型（CHECKING/SAVINGS）',
    bank_name VARCHAR(255) COMMENT '银行名称',
    is_primary BOOLEAN DEFAULT FALSE COMMENT '是否为主账户',
    is_verified BOOLEAN DEFAULT FALSE COMMENT '是否已验证',
    status VARCHAR(50) DEFAULT 'ACTIVE' COMMENT '账户状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by CHAR(36) COMMENT '创建人UUID',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by CHAR(36) COMMENT '更新人UUID',
    is_deleted BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (merchant_id) REFERENCES merchants(id),
    INDEX idx_bank_accounts_merchant (merchant_id, is_deleted),
    INDEX idx_bank_accounts_primary (merchant_id, is_primary, is_deleted),
    INDEX idx_bank_accounts_status (status, is_verified, is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商家银行账户表';

-- 1.3 门店表 (stores) - 使用UUID主键
CREATE TABLE stores (
    id CHAR(36) NOT NULL PRIMARY KEY COMMENT '门店ID（UUID格式，如LOC-xxx）',
    merchant_id CHAR(36) NOT NULL COMMENT '所属商家ID',
    store_name VARCHAR(255) NOT NULL COMMENT '门店名称',
    address VARCHAR(255) COMMENT '门店地址',
    timezone VARCHAR(64) DEFAULT 'UTC' COMMENT '时区',
    status VARCHAR(50) DEFAULT 'ACTIVE' COMMENT '门店状态',
    tax_rate DECIMAL(5,4) DEFAULT 0.0000 COMMENT '默认税率',
    currency VARCHAR(3) DEFAULT 'USD' COMMENT '币种', 
    business_hours JSON COMMENT '营业时间配置',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by CHAR(36) COMMENT '创建人UUID',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by CHAR(36) COMMENT '更新人UUID',
    is_deleted BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (merchant_id) REFERENCES merchants(id),
    INDEX idx_stores_merchant (merchant_id, is_deleted),
    INDEX idx_stores_status (status, is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='门店表（UUID主键）';

-- 1.4 用户表 (users) - 门店员工，使用UUID外键
CREATE TABLE users (
    user_id CHAR(36) NOT NULL PRIMARY KEY COMMENT '用户ID（UUID格式）',
    merchant_id CHAR(36) NOT NULL COMMENT '商家ID',
    store_id CHAR(36) NOT NULL COMMENT '门店ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名（唯一）',
    email VARCHAR(100) NOT NULL COMMENT '邮箱（唯一）',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
    pin_hash VARCHAR(255) COMMENT 'PIN码哈希(iPad登录)',
    first_name VARCHAR(50) COMMENT '名',
    last_name VARCHAR(50) COMMENT '姓',
    role VARCHAR(50) NOT NULL DEFAULT 'STAFF' COMMENT '角色',
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE' COMMENT '账号状态',
    salary DECIMAL(10,2) COMMENT '薪资',
    hire_date DATE COMMENT '入职日期',
    last_login_at TIMESTAMP COMMENT '最后登录时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by CHAR(36) COMMENT '创建人UUID',
    updated_by CHAR(36) COMMENT '更新人UUID',
    is_deleted BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (merchant_id) REFERENCES merchants(id),
    FOREIGN KEY (store_id) REFERENCES stores(id),
    UNIQUE INDEX idx_users_username (merchant_id, username),
    UNIQUE INDEX idx_users_email (merchant_id, email),
    INDEX idx_users_store (store_id),
    INDEX idx_users_login (email, password_hash, is_deleted),
    INDEX idx_users_pin_login (pin_hash, store_id, is_deleted),
    INDEX idx_users_store_status (store_id, status, is_deleted, hire_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表（门店员工，UUID外键）';

-- 1.3 角色表 (roles) - 权限角色
CREATE TABLE roles (
    role_id CHAR(36) NOT NULL PRIMARY KEY COMMENT '角色主键（UUID）',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL COMMENT '角色代码',
    description TEXT COMMENT '角色描述',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by CHAR(36) COMMENT '创建人UUID',
    updated_by CHAR(36) COMMENT '更新人UUID',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '软删除标识',
    
    UNIQUE KEY uk_roles_code (role_code),
    INDEX idx_roles_active (is_active, is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';

-- 1.4 权限表 (permissions) - 细粒度权限
CREATE TABLE permissions (
    permission_id CHAR(36) NOT NULL PRIMARY KEY COMMENT '权限主键（UUID）',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(50) NOT NULL COMMENT '权限代码',
    resource VARCHAR(50) NOT NULL COMMENT '资源标识',
    action VARCHAR(50) NOT NULL COMMENT '操作标识',
    description TEXT COMMENT '权限描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by CHAR(36) COMMENT '创建人UUID',
    updated_by CHAR(36) COMMENT '更新人UUID',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '软删除标识',
    
    UNIQUE KEY uk_permissions_code (permission_code),
    UNIQUE KEY uk_permissions_resource_action (resource, action),
    INDEX idx_permissions_resource (resource),
    INDEX idx_permissions_resource_action (resource, action, permission_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限表';

-- 1.5 用户角色关联表 (user_roles)
CREATE TABLE user_roles (
    user_id CHAR(36) NOT NULL COMMENT '用户ID（UUID）',
    role_id CHAR(36) NOT NULL COMMENT '角色ID',
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
    assigned_by CHAR(36) COMMENT '分配人UUID',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id),
    INDEX idx_user_roles_active (user_id, role_id, is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';

-- 1.6 角色权限关联表 (role_permissions)
CREATE TABLE role_permissions (
    role_id CHAR(36) NOT NULL COMMENT '角色ID',
    permission_id CHAR(36) NOT NULL COMMENT '权限ID',
    granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '授权时间',
    granted_by CHAR(36) COMMENT '授权人UUID',
    
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id),
    FOREIGN KEY (permission_id) REFERENCES permissions(permission_id),
    INDEX idx_role_permissions_lookup (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关联表';

-- =================================
-- 2. 商品与库存管理模块
-- =================================

-- 2.1 商品分类表 (categories)
CREATE TABLE categories (
    category_id CHAR(36) NOT NULL PRIMARY KEY COMMENT '分类主键（UUID）',
    store_id CHAR(36) NOT NULL COMMENT '所属店铺',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description TEXT COMMENT '分类描述',
    display_order INT DEFAULT 0 COMMENT '显示顺序',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by CHAR(36) COMMENT '创建人UUID',
    updated_by CHAR(36) COMMENT '更新人UUID',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '软删除标识',
    
    FOREIGN KEY (store_id) REFERENCES stores(id),
    INDEX idx_categories_store (store_id, is_active, is_deleted),
    INDEX idx_categories_order (display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品分类表';

-- 2.2 商品表 (products)
CREATE TABLE products (
    product_id CHAR(36) NOT NULL PRIMARY KEY COMMENT '商品ID（UUID）',
    merchant_id CHAR(36) NOT NULL COMMENT '商家ID',
    store_id CHAR(36) NOT NULL,
    category_id CHAR(36) COMMENT '商品分类',
    product_name VARCHAR(200) NOT NULL COMMENT '商品名称',
    description VARCHAR(255) DEFAULT NULL COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '销售价格',
    image_url VARCHAR(500) COMMENT '商品图片URL',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否上架',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by CHAR(36) DEFAULT NULL COMMENT '创建人UUID',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by CHAR(36) DEFAULT NULL COMMENT '更新人UUID',
    is_deleted BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (merchant_id) REFERENCES merchants(id),
    FOREIGN KEY (store_id) REFERENCES stores(id),
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
    INDEX idx_products_store (merchant_id, store_id),
    INDEX idx_products_pos_list (store_id, category_id, is_active, is_deleted, product_name, price),
    INDEX idx_products_sync (store_id, updated_at, is_deleted),
    FULLTEXT INDEX idx_products_search (product_name, description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品表';

-- 2.3 库存表 (inventory)
CREATE TABLE inventory (
    inventory_id CHAR(36) NOT NULL PRIMARY KEY COMMENT '库存主键（UUID）',
    product_id CHAR(36) NOT NULL COMMENT '商品ID',
    current_stock INT NOT NULL DEFAULT 0 COMMENT '当前库存',
    min_stock INT DEFAULT 0 COMMENT '最低库存阈值',
    max_stock INT DEFAULT 0 COMMENT '最高库存阈值',
    cost_price DECIMAL(10,2) COMMENT '成本价格',
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by CHAR(36) COMMENT '创建人UUID',
    updated_by CHAR(36) COMMENT '更新人UUID',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '软删除标识',
    
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    UNIQUE KEY uk_inventory_product (product_id),
    INDEX idx_inventory_stock_level (current_stock, min_stock),
    INDEX idx_inventory_alert (current_stock, min_stock, product_id),
    INDEX idx_inventory_cost (product_id, cost_price, current_stock)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='库存表';

-- =================================
-- 3. 订单与支付管理模块
-- =================================

-- 3.1 客户表 (customers)
CREATE TABLE customers (
    customer_id CHAR(36) NOT NULL PRIMARY KEY COMMENT '客户主键（UUID）',
    store_id CHAR(36) NOT NULL COMMENT '所属店铺',
    customer_name VARCHAR(100) NOT NULL COMMENT '客户姓名',
    phone VARCHAR(20) COMMENT '手机号码',
    email VARCHAR(100) COMMENT '邮箱地址',
    points_balance INT DEFAULT 0 COMMENT '积分余额',
    membership_level VARCHAR(20) DEFAULT 'REGULAR' COMMENT '会员等级',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by CHAR(36) COMMENT '创建人UUID',
    updated_by CHAR(36) COMMENT '更新人UUID',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '软删除标识',
    
    FOREIGN KEY (store_id) REFERENCES stores(id),
    INDEX idx_customers_store (store_id, is_deleted),
    INDEX idx_customers_phone (phone),
    INDEX idx_customers_email (email),
    INDEX idx_customers_contact (store_id, phone, email, is_deleted),
    INDEX idx_customers_membership (store_id, membership_level, points_balance, is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='客户表';

-- 3.2 订单表 (orders)
CREATE TABLE orders (
    order_id CHAR(36) NOT NULL PRIMARY KEY COMMENT '订单ID（UUID）',
    merchant_id CHAR(36) NOT NULL COMMENT '商家ID',
    store_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL COMMENT '下单用户',
    customer_id CHAR(36) COMMENT '客户ID',
    order_number VARCHAR(50) COMMENT '订单编号',
    idempotency_key VARCHAR(100) COMMENT '幂等性键',
    total_amount DECIMAL(12,2) NOT NULL COMMENT '订单总金额',
    tax_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '税费金额',
    tip_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '小费金额',
    discount_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '折扣金额',
    status ENUM('CREATED', 'CONFIRMED', 'PREPARING', 'READY', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'CREATED' COMMENT '订单状态',
    payment_status ENUM('PENDING', 'PAID', 'PARTIAL', 'REFUNDED', 'FAILED') DEFAULT 'PENDING' COMMENT '支付状态',
    order_type ENUM('DINE_IN', 'TAKEOUT', 'DELIVERY') DEFAULT 'DINE_IN' COMMENT '订单类型',
    completed_at TIMESTAMP COMMENT '完成时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by CHAR(36) DEFAULT NULL COMMENT '创建人UUID',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by CHAR(36) DEFAULT NULL COMMENT '更新人UUID',
    is_deleted BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (merchant_id) REFERENCES merchants(id),
    FOREIGN KEY (store_id) REFERENCES stores(id),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    UNIQUE KEY uk_orders_number (order_number),
    UNIQUE KEY uk_orders_idempotency (idempotency_key),
    INDEX idx_orders_store (merchant_id, store_id),
    INDEX idx_orders_user (user_id),
    INDEX idx_orders_dashboard (store_id, created_at, status, is_deleted),
    INDEX idx_orders_status_processing (status, payment_status, store_id, updated_at),
    INDEX idx_orders_user_history (user_id, created_at, status),
    INDEX idx_orders_customer_history (customer_id, created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单表';

-- 3.3 订单明细表 (order_items)
CREATE TABLE order_items (
    order_item_id CHAR(36) NOT NULL PRIMARY KEY COMMENT '订单项ID（UUID）',
    merchant_id CHAR(36) NOT NULL COMMENT '商家ID',
    store_id CHAR(36) NOT NULL,
    order_id CHAR(36) NOT NULL COMMENT '订单ID',
    product_id CHAR(36) NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    subtotal DECIMAL(12,2) NOT NULL COMMENT '小计金额',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by CHAR(36) DEFAULT NULL COMMENT '创建人UUID',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by CHAR(36) DEFAULT NULL COMMENT '更新人UUID',
    is_deleted BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    INDEX idx_order_items_order (order_id),
    INDEX idx_order_items_product (product_id),
    INDEX idx_order_items_detail (order_id, product_id, quantity, unit_price, subtotal, is_deleted),
    INDEX idx_order_items_product_stats (product_id, created_at, quantity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单明细表';

-- 3.4 支付表 (payments)
CREATE TABLE payments (
    payment_id CHAR(36) NOT NULL PRIMARY KEY COMMENT '支付主键（UUID）',
    order_id CHAR(36) NOT NULL COMMENT '订单ID',
    idempotency_key VARCHAR(100) NOT NULL COMMENT '幂等性键',
    payment_method VARCHAR(50) NOT NULL COMMENT '支付方式',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    transaction_id VARCHAR(100) COMMENT '第三方交易ID',
    status ENUM('PENDING', 'SUCCESS', 'FAILED', 'CANCELLED', 'REFUNDED') DEFAULT 'PENDING' COMMENT '支付状态',
    processed_at TIMESTAMP COMMENT '处理时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by CHAR(36) COMMENT '创建人UUID',
    updated_by CHAR(36) COMMENT '更新人UUID',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '软删除标识',
    
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    UNIQUE KEY uk_payments_idempotency (idempotency_key),
    INDEX idx_payments_order (order_id),
    INDEX idx_payments_status (status, processed_at),
    INDEX idx_payments_transaction (transaction_id),
    INDEX idx_payments_status_time (status, processed_at, order_id),
    INDEX idx_payments_transaction_lookup (transaction_id, status, amount),
    INDEX idx_payments_refund (order_id, status, amount)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付表';

-- 3.5 优惠券表 (coupons)
CREATE TABLE coupons (
    coupon_id CHAR(36) NOT NULL PRIMARY KEY COMMENT '优惠券主键（UUID）',
    store_id CHAR(36) NOT NULL COMMENT '所属店铺',
    coupon_code VARCHAR(50) NOT NULL COMMENT '优惠券代码',
    discount_type ENUM('FIXED_AMOUNT', 'PERCENTAGE') NOT NULL COMMENT '折扣类型',
    discount_value DECIMAL(10,2) NOT NULL COMMENT '折扣值',
    min_order_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '最低订单金额',
    valid_from TIMESTAMP NOT NULL COMMENT '生效时间',
    valid_until TIMESTAMP NOT NULL COMMENT '失效时间',
    usage_limit INT DEFAULT 0 COMMENT '使用次数限制(0=无限制)',
    used_count INT DEFAULT 0 COMMENT '已使用次数',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by CHAR(36) COMMENT '创建人UUID',
    updated_by CHAR(36) COMMENT '更新人UUID',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '软删除标识',
    
    FOREIGN KEY (store_id) REFERENCES stores(id),
    UNIQUE KEY uk_coupons_code (coupon_code),
    INDEX idx_coupons_store (store_id, is_active, is_deleted),
    INDEX idx_coupons_validity (valid_from, valid_until)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='优惠券表';

-- 3.6 订单优惠券关联表 (order_coupons)
CREATE TABLE order_coupons (
    order_id CHAR(36) NOT NULL COMMENT '订单ID',
    coupon_id CHAR(36) NOT NULL COMMENT '优惠券ID',
    discount_applied DECIMAL(10,2) NOT NULL COMMENT '实际折扣金额',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '使用时间',
    created_by CHAR(36) COMMENT '操作人UUID',
    
    PRIMARY KEY (order_id, coupon_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (coupon_id) REFERENCES coupons(coupon_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单优惠券关联表';

-- =================================
-- 4. 考勤与会话管理模块
-- =================================

-- 4.1 考勤表 (attendance)
CREATE TABLE attendance (
    attendance_id CHAR(36) NOT NULL PRIMARY KEY COMMENT '考勤主键（UUID）',
    user_id CHAR(36) NOT NULL COMMENT '员工ID',
    store_id CHAR(36) NOT NULL COMMENT '所属店铺',
    clock_in_time TIMESTAMP NOT NULL COMMENT '上班打卡时间',
    clock_out_time TIMESTAMP COMMENT '下班打卡时间',
    total_hours DECIMAL(5,2) COMMENT '工作总时长',
    idempotency_key VARCHAR(100) NOT NULL COMMENT '幂等性键',
    sync_status ENUM('SYNCED', 'PENDING', 'FAILED') DEFAULT 'SYNCED' COMMENT '同步状态',
    clock_in_month INT GENERATED ALWAYS AS (YEAR(clock_in_time) * 100 + MONTH(clock_in_time)) STORED COMMENT '打卡年月(YYYYMM)',
    clock_in_date DATE GENERATED ALWAYS AS (DATE(clock_in_time)) STORED COMMENT '打卡日期',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by CHAR(36) COMMENT '创建人UUID',
    updated_by CHAR(36) COMMENT '更新人UUID',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '软删除标识',
    
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (store_id) REFERENCES stores(id),
    UNIQUE KEY uk_attendance_idempotency (idempotency_key),
    INDEX idx_attendance_user_date (user_id, clock_in_time),
    INDEX idx_attendance_store_date (store_id, clock_in_time),
    INDEX idx_attendance_sync (sync_status),
    INDEX idx_attendance_monthly (user_id, clock_in_month, total_hours),
    INDEX idx_attendance_store_daily (store_id, clock_in_date, clock_in_time),
    INDEX idx_attendance_incomplete (user_id, clock_in_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='考勤表';

-- 4.2 用户会话表 (user_sessions)
CREATE TABLE user_sessions (
    session_id CHAR(36) NOT NULL PRIMARY KEY COMMENT '会话主键（UUID）',
    user_id CHAR(36) NOT NULL COMMENT '用户ID',
    device_id CHAR(36) COMMENT '设备ID',
    token_hash VARCHAR(255) NOT NULL COMMENT 'Token哈希',
    expires_at TIMESTAMP NOT NULL COMMENT '过期时间',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否活跃',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '最后活跃时间',
    
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    INDEX idx_sessions_user (user_id, is_active),
    INDEX idx_sessions_token (token_hash),
    INDEX idx_sessions_expires (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户会话表';

-- 4.3 交班记录表 (closings)
CREATE TABLE closings (
    closing_id CHAR(36) NOT NULL PRIMARY KEY COMMENT '交班主键（UUID）',
    user_id CHAR(36) NOT NULL COMMENT '交班员工ID',
    store_id CHAR(36) NOT NULL COMMENT '所属店铺',
    closing_date DATE NOT NULL COMMENT '交班日期',
    cash_counted DECIMAL(10,2) NOT NULL COMMENT '实际现金金额',
    cash_expected DECIMAL(10,2) NOT NULL COMMENT '预期现金金额',
    difference DECIMAL(10,2) NOT NULL COMMENT '差额',
    sync_status ENUM('SYNCED', 'PENDING', 'FAILED') DEFAULT 'SYNCED' COMMENT '同步状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by CHAR(36) COMMENT '创建人UUID',
    updated_by CHAR(36) COMMENT '更新人UUID',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '软删除标识',
    
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (store_id) REFERENCES stores(id),
    INDEX idx_closings_store_date (store_id, closing_date),
    INDEX idx_closings_user (user_id),
    INDEX idx_closings_sync (sync_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='交班记录表';

-- 4.4 收据记录表 (receipts)
CREATE TABLE receipts (
    receipt_id CHAR(36) NOT NULL PRIMARY KEY COMMENT '收据主键（UUID）',
    order_id CHAR(36) NOT NULL COMMENT '订单ID',
    delivery_method ENUM('PRINT', 'SMS', 'EMAIL') NOT NULL COMMENT '发送方式',
    recipient VARCHAR(200) COMMENT '接收方',
    sent_at TIMESTAMP COMMENT '发送时间',
    status ENUM('PENDING', 'SENT', 'FAILED') DEFAULT 'PENDING' COMMENT '发送状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by CHAR(36) COMMENT '创建人UUID',
    updated_by CHAR(36) COMMENT '更新人UUID',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '软删除标识',
    
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    INDEX idx_receipts_order (order_id),
    INDEX idx_receipts_status (status, sent_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='收据记录表';

-- =================================
-- 5. 系统管理与配置模块
-- =================================

-- 5.1 设备表 (devices)
CREATE TABLE devices (
    device_id CHAR(36) NOT NULL PRIMARY KEY COMMENT '设备主键（UUID）',
    store_id CHAR(36) NOT NULL COMMENT '所属店铺',
    device_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    device_type VARCHAR(50) NOT NULL COMMENT '设备类型',
    mac_address VARCHAR(17) COMMENT 'MAC地址',
    ip_address VARCHAR(15) COMMENT 'IP地址',
    last_online TIMESTAMP COMMENT '最后在线时间',
    status VARCHAR(20) NOT NULL DEFAULT 'OFFLINE' COMMENT '设备状态',
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by CHAR(36) COMMENT '创建人UUID',
    updated_by CHAR(36) COMMENT '更新人UUID',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '软删除标识',
    
    FOREIGN KEY (store_id) REFERENCES stores(id),
    INDEX idx_devices_store (store_id, is_deleted),
    INDEX idx_devices_status (status, last_online),
    INDEX idx_devices_mac (mac_address),
    INDEX idx_devices_status_monitoring (store_id, status, last_online, device_type),
    INDEX idx_devices_mac_lookup (mac_address)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='设备表';

-- 添加会话表与设备表的外键约束
ALTER TABLE user_sessions ADD CONSTRAINT fk_sessions_device 
FOREIGN KEY (device_id) REFERENCES devices(device_id);

-- 5.2 设备码表 (device_codes) - Square风格激活码
CREATE TABLE device_codes (
    device_code_id CHAR(36) NOT NULL PRIMARY KEY COMMENT '设备码主键（UUID）',
    device_code VARCHAR(12) NOT NULL UNIQUE COMMENT '设备激活码，12位数字字母混合',
    device_id CHAR(36) COMMENT '关联的设备ID（可选）',
    device_fingerprint VARCHAR(255) COMMENT '设备指纹信息',
    status VARCHAR(20) NOT NULL DEFAULT 'UNUSED' COMMENT '设备码状态：UNUSED/BOUND/EXPIRED',
    activation_attempts INT DEFAULT 0 COMMENT '激活尝试次数',
    max_attempts INT DEFAULT 3 COMMENT '最大激活尝试次数',
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '发行时间',
    expired_at TIMESTAMP COMMENT '过期时间',
    bound_at TIMESTAMP COMMENT '绑定时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by CHAR(36) COMMENT '创建人UUID',
    updated_by CHAR(36) COMMENT '更新人UUID',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '软删除标识',
    
    FOREIGN KEY (device_id) REFERENCES devices(device_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id),
    FOREIGN KEY (updated_by) REFERENCES users(user_id),
    
    INDEX idx_device_codes_code (device_code),
    INDEX idx_device_codes_device (device_id, status),
    INDEX idx_device_codes_status (status, expired_at),
    INDEX idx_device_codes_fingerprint (device_fingerprint),
    INDEX idx_device_codes_attempts (activation_attempts, status),
    INDEX idx_device_codes_issued (issued_at, status),
    UNIQUE KEY uk_device_codes_code (device_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='设备激活码表，Square风格即时激活';

-- 5.3 税务规则表 (tax_rules)
CREATE TABLE tax_rules (
    tax_rule_id CHAR(36) NOT NULL PRIMARY KEY COMMENT '税务规则主键（UUID）',
    store_id CHAR(36) NOT NULL COMMENT '所属店铺',
    tax_name VARCHAR(100) NOT NULL COMMENT '税种名称',
    tax_rate DECIMAL(5,4) NOT NULL COMMENT '税率',
    tax_type ENUM('STATE_TAX', 'CITY_TAX', 'FEDERAL_TAX', 'VAT') NOT NULL COMMENT '税种类型',
    applicable_to VARCHAR(100) COMMENT '适用范围',
    effective_from DATE NOT NULL COMMENT '生效日期',
    effective_until DATE COMMENT '失效日期',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by CHAR(36) COMMENT '创建人UUID',
    updated_by CHAR(36) COMMENT '更新人UUID',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '软删除标识',
    
    FOREIGN KEY (store_id) REFERENCES stores(id),
    INDEX idx_tax_rules_store (store_id, is_active, is_deleted),
    INDEX idx_tax_rules_effective (effective_from, effective_until)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='税务规则表';

-- 5.3 通知表 (notifications)
CREATE TABLE notifications (
    notification_id CHAR(36) NOT NULL PRIMARY KEY COMMENT '通知主键（UUID）',
    store_id CHAR(36) COMMENT '所属店铺',
    user_id CHAR(36) COMMENT '目标用户',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    message TEXT NOT NULL COMMENT '通知内容',
    type VARCHAR(50) NOT NULL COMMENT '通知类型',
    is_read BOOLEAN DEFAULT FALSE COMMENT '是否已读',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by CHAR(36) COMMENT '创建人UUID',
    updated_by CHAR(36) COMMENT '更新人UUID',
    read_at TIMESTAMP COMMENT '阅读时间',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '软删除标识',
    
    FOREIGN KEY (store_id) REFERENCES stores(id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    INDEX idx_notifications_user (user_id, is_read, created_at),
    INDEX idx_notifications_store (store_id, type, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知表';

-- =================================
-- 6. 报表与汇总模块
-- =================================

-- 6.1 日销售汇总表 (daily_sales_reports)
CREATE TABLE daily_sales_reports (
    report_id CHAR(36) NOT NULL PRIMARY KEY COMMENT '报表主键（UUID）',
    store_id CHAR(36) NOT NULL COMMENT '所属店铺',
    report_date DATE NOT NULL COMMENT '报表日期',
    total_sales_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '总销售额',
    total_orders INT NOT NULL DEFAULT 0 COMMENT '订单总数',
    average_order_value DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '客单价',
    total_tips DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '小费总额',
    top_product_id CHAR(36) COMMENT '热门商品ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by CHAR(36) COMMENT '创建人UUID',
    updated_by CHAR(36) COMMENT '更新人UUID',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '软删除标识',
    
    FOREIGN KEY (store_id) REFERENCES stores(id),
    FOREIGN KEY (top_product_id) REFERENCES products(product_id),
    UNIQUE KEY uk_daily_reports_store_date (store_id, report_date),
    INDEX idx_daily_reports_date (report_date),
    INDEX idx_daily_reports_store (store_id, report_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='日销售汇总表';

-- =================================
-- 7. 性能监控和备份表
-- =================================

-- 7.1 性能监控表
CREATE TABLE performance_metrics (
    metric_id CHAR(36) NOT NULL PRIMARY KEY COMMENT '指标ID（UUID）',
    metric_name VARCHAR(100) NOT NULL,
    metric_value DECIMAL(15,4) NOT NULL,
    store_id CHAR(36),
    measured_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_performance_metrics_name_time (metric_name, measured_at),
    INDEX idx_performance_metrics_store (store_id, measured_at)
) ENGINE=InnoDB COMMENT='性能指标监控表';

-- 7.2 备份历史记录表
CREATE TABLE backup_history (
    backup_id CHAR(36) NOT NULL PRIMARY KEY COMMENT '备份ID（UUID）',
    backup_type ENUM('FULL', 'INCREMENTAL', 'DIFFERENTIAL') NOT NULL,
    backup_path VARCHAR(500) NOT NULL,
    backup_size BIGINT COMMENT '备份文件大小(字节)',
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    status ENUM('RUNNING', 'COMPLETED', 'FAILED') DEFAULT 'RUNNING',
    error_message TEXT,
    created_by BIGINT,
    
    INDEX idx_backup_history_time (start_time DESC),
    INDEX idx_backup_history_type (backup_type, status)
) ENGINE=InnoDB COMMENT='备份历史记录表';

-- 7.3 系统告警表
CREATE TABLE system_alerts (
    alert_id CHAR(36) NOT NULL PRIMARY KEY COMMENT '警告ID（UUID）',
    alert_type VARCHAR(50) NOT NULL,
    alert_level ENUM('INFO', 'WARNING', 'ERROR', 'CRITICAL') NOT NULL,
    alert_message TEXT NOT NULL,
    alert_data JSON,
    resolved BOOLEAN DEFAULT FALSE,
    resolved_at TIMESTAMP NULL,
    resolved_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_system_alerts_type_level (alert_type, alert_level, created_at),
    INDEX idx_system_alerts_resolved (resolved, created_at)
) ENGINE=InnoDB COMMENT='系统告警记录表';

-- =================================
-- 8. JSON字段虚拟列优化
-- =================================

-- 店铺营业时间虚拟列
ALTER TABLE stores 
ADD COLUMN monday_open TIME GENERATED ALWAYS AS (STR_TO_DATE(JSON_UNQUOTE(JSON_EXTRACT(business_hours, '$.monday.open')), '%H:%i')) VIRTUAL,
ADD COLUMN monday_close TIME GENERATED ALWAYS AS (STR_TO_DATE(JSON_UNQUOTE(JSON_EXTRACT(business_hours, '$.monday.close')), '%H:%i')) VIRTUAL;

CREATE INDEX idx_stores_monday_hours ON stores(monday_open, monday_close);
-- 使用虚拟列建索引，而非表达式索引
CREATE INDEX idx_stores_monday_open ON stores(monday_open);

-- =================================
-- 9. CHECK约束
-- =================================

-- 价格约束
ALTER TABLE products ADD CONSTRAINT chk_products_price_positive CHECK (price > 0);
ALTER TABLE order_items ADD CONSTRAINT chk_order_items_price_positive CHECK (unit_price > 0 AND subtotal >= 0);
ALTER TABLE payments ADD CONSTRAINT chk_payments_amount_positive CHECK (amount > 0);

-- 库存约束
ALTER TABLE inventory ADD CONSTRAINT chk_inventory_stock_non_negative CHECK (current_stock >= 0);
ALTER TABLE inventory ADD CONSTRAINT chk_inventory_thresholds CHECK (min_stock >= 0 AND max_stock >= min_stock);

-- 税率约束
ALTER TABLE tax_rules ADD CONSTRAINT chk_tax_rate_range CHECK (tax_rate >= 0 AND tax_rate <= 1);
ALTER TABLE stores ADD CONSTRAINT chk_store_tax_rate_range CHECK (tax_rate >= 0 AND tax_rate <= 1);

-- 优惠券约束
ALTER TABLE coupons ADD CONSTRAINT chk_coupon_discount CHECK (
    (discount_type = 'FIXED_AMOUNT' AND discount_value > 0) OR 
    (discount_type = 'PERCENTAGE' AND discount_value > 0 AND discount_value <= 100)
);

-- 考勤时间约束
ALTER TABLE attendance ADD CONSTRAINT chk_attendance_time_order CHECK (
    clock_out_time IS NULL OR clock_out_time >= clock_in_time
);

-- 会话过期时间约束
ALTER TABLE user_sessions ADD CONSTRAINT chk_session_expires CHECK (expires_at > created_at);

-- =================================
-- 10. 触发器
-- =================================

DELIMITER //

-- 订单金额一致性检查
CREATE TRIGGER trg_order_amount_consistency
BEFORE UPDATE ON orders
FOR EACH ROW
BEGIN
    DECLARE calculated_total DECIMAL(10,2);
    
    SELECT IFNULL(SUM(subtotal), 0) INTO calculated_total
    FROM order_items 
    WHERE order_id = NEW.order_id AND is_deleted = FALSE;
    
    IF ABS(calculated_total - (NEW.total_amount - NEW.tax_amount - NEW.tip_amount + NEW.discount_amount)) > calculated_total * 0.05 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '订单总金额与订单项金额不一致';
    END IF;
END//

-- 库存扣减触发器
CREATE TRIGGER trg_inventory_deduction
AFTER INSERT ON order_items
FOR EACH ROW
BEGIN
    UPDATE inventory 
    SET current_stock = current_stock - NEW.quantity,
        updated_at = CURRENT_TIMESTAMP,
        updated_by = NEW.created_by
    WHERE product_id = NEW.product_id;
    
    IF (SELECT current_stock FROM inventory WHERE product_id = NEW.product_id) < 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '商品库存不足';
    END IF;
END//

-- 优惠券使用次数更新
CREATE TRIGGER trg_coupon_usage_update
AFTER INSERT ON order_coupons
FOR EACH ROW
BEGIN
    UPDATE coupons 
    SET used_count = used_count + 1,
        updated_at = CURRENT_TIMESTAMP,
        updated_by = NEW.created_by
    WHERE coupon_id = NEW.coupon_id;
    
    IF EXISTS (
        SELECT 1 FROM coupons 
        WHERE coupon_id = NEW.coupon_id 
          AND usage_limit > 0 
          AND used_count > usage_limit
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '优惠券使用次数已达上限';
    END IF;
END//

-- 考勤工时自动计算
CREATE TRIGGER trg_attendance_hours_calculation
BEFORE UPDATE ON attendance
FOR EACH ROW
BEGIN
    IF NEW.clock_out_time IS NOT NULL AND OLD.clock_out_time IS NULL THEN
        SET NEW.total_hours = TIMESTAMPDIFF(MINUTE, NEW.clock_in_time, NEW.clock_out_time) / 60.0;
    END IF;
END//

DELIMITER ;

-- =================================
-- 11. 存储过程
-- =================================

DELIMITER //

-- 日销售汇总存储过程
CREATE PROCEDURE sp_generate_daily_sales_report(
    IN p_store_id CHAR(36),
    IN p_report_date DATE
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    DELETE FROM daily_sales_reports 
    WHERE store_id = p_store_id AND report_date = p_report_date;
    
    INSERT INTO daily_sales_reports (
        store_id, report_date, total_sales_amount, total_orders, 
        average_order_value, total_tips, top_product_id, created_by
    )
    SELECT 
        p_store_id,
        p_report_date,
        IFNULL(SUM(o.total_amount), 0) as total_sales_amount,
        COUNT(o.order_id) as total_orders,
        IFNULL(AVG(o.total_amount), 0) as average_order_value,
        IFNULL(SUM(o.tip_amount), 0) as total_tips,
        (SELECT oi.product_id 
         FROM order_items oi 
         JOIN orders o2 ON oi.order_id = o2.order_id 
         WHERE o2.store_id = p_store_id 
           AND DATE(o2.created_at) = p_report_date
           AND o2.status = 'COMPLETED'
           AND oi.is_deleted = FALSE
         GROUP BY oi.product_id 
         ORDER BY SUM(oi.quantity) DESC 
         LIMIT 1) as top_product_id,
        1 as created_by
    FROM orders o
    WHERE o.store_id = p_store_id 
      AND DATE(o.created_at) = p_report_date
      AND o.status = 'COMPLETED'
      AND o.is_deleted = FALSE;
    
    COMMIT;
END//

-- 库存预警检查存储过程
CREATE PROCEDURE sp_check_inventory_alerts(
    IN p_store_id CHAR(36)
)
BEGIN
    SELECT 
        p.id as product_id,
        p.product_name,
        i.current_stock,
        i.min_stock,
        c.category_name
    FROM inventory i
    JOIN products p ON i.product_id = p.product_id
    JOIN categories c ON p.category_id = c.category_id
    WHERE p.store_id = p_store_id
      AND i.current_stock <= i.min_stock
      AND p.is_active = TRUE
      AND p.is_deleted = FALSE
    ORDER BY (i.current_stock / NULLIF(i.min_stock, 0)) ASC;
END//

-- 用户权限检查存储过程
CREATE PROCEDURE sp_check_user_permission(
    IN p_user_id CHAR(36),
    IN p_resource VARCHAR(50),
    IN p_action VARCHAR(50),
    OUT p_has_permission BOOLEAN
)
BEGIN
    DECLARE permission_count INT DEFAULT 0;
    
    SELECT COUNT(*) INTO permission_count
    FROM users u
    JOIN user_roles ur ON u.user_id = ur.user_id
    JOIN role_permissions rp ON ur.role_id = rp.role_id
    JOIN permissions p ON rp.permission_id = p.permission_id
    WHERE u.user_id = p_user_id
      AND p.resource = p_resource
      AND p.action = p_action
      AND u.is_deleted = FALSE
      AND ur.is_active = TRUE
      AND p.is_deleted = FALSE;
    
    SET p_has_permission = (permission_count > 0);
END//

DELIMITER ;

-- =================================
-- 12. 优化视图
-- =================================

-- 用户权限视图
CREATE VIEW v_user_permissions AS
SELECT 
    u.user_id,
    u.store_id,
    u.email,
    u.first_name,
    u.last_name,
    r.role_name,
    p.permission_code,
    p.resource,
    p.action
FROM users u
JOIN user_roles ur ON u.user_id = ur.user_id AND ur.is_active = TRUE
JOIN roles r ON ur.role_id = r.role_id AND r.is_active = TRUE
JOIN role_permissions rp ON r.role_id = rp.role_id
JOIN permissions p ON rp.permission_id = p.permission_id
WHERE u.is_deleted = FALSE
  AND r.is_deleted = FALSE
  AND p.is_deleted = FALSE;

-- 商品库存视图
CREATE VIEW v_product_inventory AS
SELECT 
    p.product_id,
    p.store_id,
    p.product_name,
    p.price,
    c.category_name,
    i.current_stock,
    i.min_stock,
    i.max_stock,
    i.cost_price,
    CASE 
        WHEN i.current_stock <= i.min_stock THEN 'LOW_STOCK'
        WHEN i.current_stock >= i.max_stock THEN 'OVERSTOCK'
        ELSE 'NORMAL'
    END as stock_status,
    p.is_active,
    p.updated_at
FROM products p
JOIN categories c ON p.category_id = c.category_id
JOIN inventory i ON p.product_id = i.product_id
WHERE p.is_deleted = FALSE
  AND c.is_deleted = FALSE
  AND i.is_deleted = FALSE;

-- 订单详情视图
CREATE VIEW v_order_details AS
SELECT 
    o.order_id,
    o.store_id,
    o.order_number,
    o.total_amount,
    o.status as order_status,
    o.payment_status,
    o.created_at,
    CONCAT(u.first_name, ' ', u.last_name) as cashier_name,
    c.customer_name,
    c.phone as customer_phone,
    COUNT(oi.order_item_id) as item_count,
    SUM(oi.quantity) as total_quantity
FROM orders o
LEFT JOIN users u ON o.user_id = u.user_id
LEFT JOIN customers c ON o.customer_id = c.customer_id
LEFT JOIN order_items oi ON o.order_id = oi.order_id AND oi.is_deleted = FALSE
WHERE o.is_deleted = FALSE
GROUP BY o.order_id, o.store_id, o.order_number, o.total_amount, 
         o.status, o.payment_status, o.created_at, 
         u.first_name, u.last_name, c.customer_name, c.phone;

-- 慢查询监控视图
CREATE VIEW v_slow_queries AS
SELECT 
    DIGEST_TEXT as query_text,
    COUNT_STAR as exec_count,
    AVG_TIMER_WAIT/1000000000000 as avg_time_sec,
    MAX_TIMER_WAIT/1000000000000 as max_time_sec,
    SUM_ROWS_EXAMINED as total_rows_examined,
    SUM_ROWS_SENT as total_rows_sent,
    FIRST_SEEN,
    LAST_SEEN
FROM performance_schema.events_statements_summary_by_digest
WHERE AVG_TIMER_WAIT > 2000000000000 -- 大于2秒的查询
ORDER BY AVG_TIMER_WAIT DESC
LIMIT 20;

-- 表空间使用情况视图
CREATE VIEW v_table_space_usage AS
SELECT 
    TABLE_SCHEMA as database_name,
    TABLE_NAME as table_name,
    ROUND(((DATA_LENGTH + INDEX_LENGTH) / 1024 / 1024), 2) as size_mb,
    TABLE_ROWS as row_count,
    ROUND(((DATA_LENGTH + INDEX_LENGTH) / TABLE_ROWS), 2) as avg_row_size,
    ENGINE
FROM information_schema.TABLES
WHERE TABLE_SCHEMA NOT IN ('information_schema', 'mysql', 'performance_schema', 'sys')
ORDER BY (DATA_LENGTH + INDEX_LENGTH) DESC;

-- 索引使用情况视图
CREATE VIEW v_index_usage AS
SELECT 
    OBJECT_SCHEMA as database_name,
    OBJECT_NAME as table_name,
    INDEX_NAME as index_name,
    COUNT_FETCH as select_count,
    COUNT_INSERT as insert_count,
    COUNT_UPDATE as update_count,
    COUNT_DELETE as delete_count
FROM performance_schema.table_io_waits_summary_by_index_usage
WHERE OBJECT_SCHEMA NOT IN ('information_schema', 'mysql', 'performance_schema', 'sys')
ORDER BY COUNT_FETCH DESC;

-- =================================
-- 13. 初始化基础数据
-- =================================

-- 基础权限数据
INSERT INTO permissions (permission_id, permission_name, permission_code, resource, action, description, created_by) VALUES
-- 用户管理权限
('PER-174000000001', '查看用户', 'user.view', 'user', 'view', '查看用户信息', 'MRC-174000000001'),
('PER-174000000002', '创建用户', 'user.create', 'user', 'create', '创建新用户', 'MRC-174000000001'),
('PER-174000000003', '编辑用户', 'user.edit', 'user', 'edit', '编辑用户信息', 'MRC-174000000001'),
('PER-174000000004', '删除用户', 'user.delete', 'user', 'delete', '删除用户', 'MRC-174000000001'),
-- 商品管理权限
('PER-174000000005', '查看商品', 'product.view', 'product', 'view', '查看商品信息', 'MRC-174000000001'),
('PER-174000000006', '创建商品', 'product.create', 'product', 'create', '创建新商品', 'MRC-174000000001'),
('PER-174000000007', '编辑商品', 'product.edit', 'product', 'edit', '编辑商品信息', 'MRC-174000000001'),
('PER-174000000008', '删除商品', 'product.delete', 'product', 'delete', '删除商品', 'MRC-174000000001'),
('PER-174000000009', '管理库存', 'inventory.manage', 'inventory', 'manage', '管理商品库存', 'MRC-174000000001'),
-- 订单管理权限
('PER-174000000010', '查看订单', 'order.view', 'order', 'view', '查看订单信息', 'MRC-174000000001'),
('PER-174000000011', '创建订单', 'order.create', 'order', 'create', '创建新订单', 'MRC-174000000001'),
('PER-174000000012', '编辑订单', 'order.edit', 'order', 'edit', '编辑订单信息', 'MRC-174000000001'),
('PER-174000000013', '取消订单', 'order.cancel', 'order', 'cancel', '取消订单', 'MRC-174000000001'),
('PER-174000000014', '处理订单', 'order.process', 'order', 'process', '处理订单状态', 'MRC-174000000001'),
-- 支付管理权限
('PER-174000000015', '查看支付', 'payment.view', 'payment', 'view', '查看支付信息', 'MRC-174000000001'),
('PER-174000000016', '处理支付', 'payment.process', 'payment', 'process', '处理支付操作', 'MRC-174000000001'),
('PER-174000000017', '退款处理', 'payment.refund', 'payment', 'refund', '处理退款', 'MRC-174000000001'),
-- 客户管理权限
('PER-174000000018', '查看客户', 'customer.view', 'customer', 'view', '查看客户信息', 'MRC-174000000001'),
('PER-174000000019', '创建客户', 'customer.create', 'customer', 'create', '创建新客户', 'MRC-174000000001'),
('PER-174000000020', '编辑客户', 'customer.edit', 'customer', 'edit', '编辑客户信息', 'MRC-174000000001'),
('PER-174000000021', '管理积分', 'customer.points', 'customer', 'points', '管理客户积分', 'MRC-174000000001'),
-- 优惠券管理权限
('PER-174000000022', '查看优惠券', 'coupon.view', 'coupon', 'view', '查看优惠券信息', 'MRC-174000000001'),
('PER-174000000023', '创建优惠券', 'coupon.create', 'coupon', 'create', '创建优惠券', 'MRC-174000000001'),
('PER-174000000024', '编辑优惠券', 'coupon.edit', 'coupon', 'edit', '编辑优惠券', 'MRC-174000000001'),
('PER-174000000025', '删除优惠券', 'coupon.delete', 'coupon', 'delete', '删除优惠券', 'MRC-174000000001'),
-- 报表权限
('PER-174000000026', '查看报表', 'report.view', 'report', 'view', '查看销售报表', 'MRC-174000000001'),
('PER-174000000027', '导出报表', 'report.export', 'report', 'export', '导出报表数据', 'MRC-174000000001'),
-- 店铺管理权限
('PER-174000000028', '查看店铺设置', 'store.view', 'store', 'view', '查看店铺设置', 'MRC-174000000001'),
('PER-174000000029', '编辑店铺设置', 'store.edit', 'store', 'edit', '编辑店铺设置', 'MRC-174000000001'),
('PER-174000000030', '管理设备', 'device.manage', 'device', 'manage', '管理POS设备', 'MRC-174000000001'),
('PER-174000000031', '管理税务', 'tax.manage', 'tax', 'manage', '管理税务规则', 'MRC-174000000001'),
-- 考勤管理权限
('PER-174000000032', '查看考勤', 'attendance.view', 'attendance', 'view', '查看考勤记录', 'MRC-174000000001'),
('PER-174000000033', '打卡操作', 'attendance.clock', 'attendance', 'clock', '执行打卡操作', 'MRC-174000000001'),
('PER-174000000034', '管理考勤', 'attendance.manage', 'attendance', 'manage', '管理员工考勤', 'MRC-174000000001'),
-- 交班权限
('PER-174000000035', '执行交班', 'closing.execute', 'closing', 'execute', '执行交班操作', 'MRC-174000000001'),
('PER-174000000036', '查看交班记录', 'closing.view', 'closing', 'view', '查看交班记录', 'MRC-174000000001');

-- 基础角色数据
INSERT INTO roles (role_id, role_name, role_code, description, created_by) VALUES
('ROL-174000000001', '超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', 'MRC-174000000001'),
('ROL-174000000002', '店长', 'STORE_MANAGER', '店铺管理员，管理店铺日常运营', 'MRC-174000000001'),
('ROL-174000000003', '收银员', 'CASHIER', '收银员，处理订单和支付', 'MRC-174000000001'),
('ROL-174000000004', '服务员', 'WAITER', '服务员，创建订单和处理客户', 'MRC-174000000001');

-- 角色权限分配
-- 超级管理员 - 所有权限
INSERT INTO role_permissions (role_id, permission_id, granted_by)
SELECT 'ROL-174000000001', permission_id, 'MRC-174000000001' FROM permissions;

-- 店长权限 (除了用户删除)
INSERT INTO role_permissions (role_id, permission_id, granted_by)
SELECT 'ROL-174000000002', permission_id, 'MRC-174000000001' FROM permissions 
WHERE permission_code NOT IN ('user.delete');

-- 收银员权限
INSERT INTO role_permissions (role_id, permission_id, granted_by)
SELECT 'ROL-174000000003', permission_id, 'MRC-174000000001' FROM permissions 
WHERE permission_code IN (
    'product.view', 'inventory.manage',
    'order.view', 'order.create', 'order.edit', 'order.process',
    'payment.view', 'payment.process', 'payment.refund',
    'customer.view', 'customer.create', 'customer.edit', 'customer.points',
    'coupon.view',
    'attendance.clock', 'closing.execute'
);

-- 服务员权限
INSERT INTO role_permissions (role_id, permission_id, granted_by)
SELECT 'ROL-174000000004', permission_id, 'MRC-174000000001' FROM permissions 
WHERE permission_code IN (
    'product.view',
    'order.view', 'order.create', 'order.edit',
    'customer.view', 'customer.create', 'customer.edit',
    'attendance.clock'
);

-- 示例商家数据（Square风格，UUID主键）
INSERT INTO merchants (id, email, password_hash, business_name, industry, currency, country, status, created_by) VALUES
('MRC-174000000001', 'owner@trip7cafe.com', '$2a$10$rMgr4YOhVNcXP7Qhp8jQHe7vKbQZ8tXkF2VyLjKrX3nP9sWqR1tGu', 'Trip7 Cafe Holdings', 'restaurant', 'USD', 'US', 'ACTIVE', 'MRC-174000000001');

-- 示例银行账户数据
INSERT INTO merchant_bank_accounts (id, merchant_id, account_number, routing_number, account_holder, account_type, bank_name, is_primary, is_verified, status, created_by) VALUES
('BA-174000000001', 'MRC-174000000001', '1234567890', '987654321', 'Trip7 Cafe Holdings', 'CHECKING', 'Bank of America', TRUE, TRUE, 'ACTIVE', 'MRC-174000000001');

-- 示例门店数据
INSERT INTO stores (id, merchant_id, store_name, address, timezone, status, tax_rate, currency, business_hours, created_by) VALUES
('LOC-174000000001', 'MRC-174000000001', '新宿店', '東京都新宿区1-2-3', 'Asia/Tokyo', 'ACTIVE', 0.0875, 'USD', 
'{"monday": {"open": "07:00", "close": "22:00"}, "tuesday": {"open": "07:00", "close": "22:00"}, "wednesday": {"open": "07:00", "close": "22:00"}, "thursday": {"open": "07:00", "close": "22:00"}, "friday": {"open": "07:00", "close": "23:00"}, "saturday": {"open": "08:00", "close": "23:00"}, "sunday": {"open": "08:00", "close": "21:00"}}', 'MRC-174000000001');

-- 示例管理员用户
INSERT INTO users (user_id, merchant_id, store_id, username, email, password_hash, pin_hash, first_name, last_name, role, status, created_by, salary, hire_date) VALUES
('USR-174000000001', 'MRC-174000000001', 'LOC-174000000001', 'admin', 'admin@trip7cafe.com', '$2a$10$rMgr4YOhVNcXP7Qhp8jQHe7vKbQZ8tXkF2VyLjKrX3nP9sWqR1tGu', '$2a$10$abcdef1234567890', 'Admin', 'User', 'OWNER', 'ACTIVE', 'MRC-174000000001', 50000.00, '2024-01-01');

-- 分配超级管理员角色
INSERT INTO user_roles (user_id, role_id, assigned_by) VALUES ('USR-174000000001', 'ROL-174000000001', 'MRC-174000000001');

-- 示例商品分类和商品
INSERT INTO categories (category_id, store_id, category_name, description, display_order, created_by) VALUES
('CAT-174000000001', 'LOC-174000000001', '咖啡', '各类咖啡饮品', 1, 'MRC-174000000001'),
('CAT-174000000002', 'LOC-174000000001', '茶饮', '茶类饮品', 2, 'MRC-174000000001'),
('CAT-174000000003', 'LOC-174000000001', '甜点', '蛋糕和甜品', 3, 'MRC-174000000001'),
('CAT-174000000004', 'LOC-174000000001', '轻食', '三明治和沙拉', 4, 'MRC-174000000001');

-- 插入商品数据
INSERT INTO products (product_id, merchant_id, store_id, category_id, product_name, description, price, is_active, created_by) VALUES
-- 咖啡类
('PRD-174000000001', 'MRC-174000000001', 'LOC-174000000001', 'CAT-174000000001', '美式咖啡', '经典美式黑咖啡', 3.50, TRUE, 'MRC-174000000001'),
('PRD-174000000002', 'MRC-174000000001', 'LOC-174000000001', 'CAT-174000000001', '拿铁', '香浓牛奶咖啡', 4.50, TRUE, 'MRC-174000000001'),
('PRD-174000000003', 'MRC-174000000001', 'LOC-174000000001', 'CAT-174000000001', '卡布奇诺', '意式卡布奇诺', 4.00, TRUE, 'MRC-174000000001'),
('PRD-174000000004', 'MRC-174000000001', 'LOC-174000000001', 'CAT-174000000001', '摩卡', '巧克力摩卡咖啡', 5.00, TRUE, 'MRC-174000000001'),
-- 茶饮类
('PRD-174000000005', 'MRC-174000000001', 'LOC-174000000001', 'CAT-174000000002', '绿茶', '清香绿茶', 2.50, TRUE, 'MRC-174000000001'),
('PRD-174000000006', 'MRC-174000000001', 'LOC-174000000001', 'CAT-174000000002', '红茶', '经典红茶', 2.50, TRUE, 'MRC-174000000001'),
('PRD-174000000007', 'MRC-174000000001', 'LOC-174000000001', 'CAT-174000000002', '柠檬茶', '清爽柠檬茶', 3.00, TRUE, 'MRC-174000000001'),
-- 甜点类
('PRD-174000000008', 'MRC-174000000001', 'LOC-174000000001', 'CAT-174000000003', '芝士蛋糕', '纽约风味芝士蛋糕', 6.50, TRUE, 'MRC-174000000001'),
('PRD-174000000009', 'MRC-174000000001', 'LOC-174000000001', 'CAT-174000000003', '巧克力蛋糕', '浓郁巧克力蛋糕', 5.50, TRUE, 'MRC-174000000001'),
('PRD-174000000010', 'MRC-174000000001', 'LOC-174000000001', 'CAT-174000000003', '提拉米苏', '意式提拉米苏', 7.00, TRUE, 'MRC-174000000001'),
-- 轻食类
('PRD-174000000011', 'MRC-174000000001', 'LOC-174000000001', 'CAT-174000000004', '火腿三明治', '经典火腿芝士三明治', 8.50, TRUE, 'MRC-174000000001'),
('PRD-174000000012', 'MRC-174000000001', 'LOC-174000000001', 'CAT-174000000004', '凯撒沙拉', '新鲜凯撒沙拉', 9.00, TRUE, 'MRC-174000000001');

-- 初始化库存数据
INSERT INTO inventory (inventory_id, product_id, current_stock, min_stock, max_stock, cost_price, created_by) VALUES
('INV-174000000001', 'PRD-174000000001', 100, 10, 500, 2.10, 'MRC-174000000001'),
('INV-174000000002', 'PRD-174000000002', 100, 10, 500, 2.70, 'MRC-174000000001'),
('INV-174000000003', 'PRD-174000000003', 100, 10, 500, 2.40, 'MRC-174000000001'),
('INV-174000000004', 'PRD-174000000004', 100, 10, 500, 3.00, 'MRC-174000000001'),
('INV-174000000005', 'PRD-174000000005', 100, 10, 500, 1.50, 'MRC-174000000001'),
('INV-174000000006', 'PRD-174000000006', 100, 10, 500, 1.50, 'MRC-174000000001'),
('INV-174000000007', 'PRD-174000000007', 100, 10, 500, 1.80, 'MRC-174000000001'),
('INV-174000000008', 'PRD-174000000008', 100, 10, 500, 3.90, 'MRC-174000000001'),
('INV-174000000009', 'PRD-174000000009', 100, 10, 500, 3.30, 'MRC-174000000001'),
('INV-174000000010', 'PRD-174000000010', 100, 10, 500, 4.20, 'MRC-174000000001'),
('INV-174000000011', 'PRD-174000000011', 100, 10, 500, 5.10, 'MRC-174000000001'),
('INV-174000000012', 'PRD-174000000012', 100, 10, 500, 5.40, 'MRC-174000000001');

-- 示例税务规则
INSERT INTO tax_rules (tax_rule_id, store_id, tax_name, tax_rate, tax_type, applicable_to, effective_from, created_by) VALUES
('TAX-174000000001', 'LOC-174000000001', '纽约州税', 0.0400, 'STATE_TAX', 'ALL', '2024-01-01', 'MRC-174000000001'),
('TAX-174000000002', 'LOC-174000000001', '纽约市税', 0.0475, 'CITY_TAX', 'ALL', '2024-01-01', 'MRC-174000000001');

-- 示例设备
INSERT INTO devices (device_id, store_id, device_name, device_type, status, created_by) VALUES
('DEV-174000000001', 'LOC-174000000001', 'POS Terminal 1', 'POS_TERMINAL', 'ONLINE', 'MRC-174000000001'),
('DEV-174000000002', 'LOC-174000000001', 'Kitchen Display 1', 'KITCHEN_DISPLAY', 'ONLINE', 'MRC-174000000001'),
('DEV-174000000003', 'LOC-174000000001', 'Receipt Printer 1', 'RECEIPT_PRINTER', 'ONLINE', 'MRC-174000000001');

-- 插入性能基准数据
INSERT INTO performance_metrics (metric_id, metric_name, metric_value, measured_at) VALUES
('MET-174000000001', 'baseline_order_insert_time_ms', 50.0, NOW()),
('MET-174000000002', 'baseline_product_search_time_ms', 20.0, NOW()),
('MET-174000000003', 'baseline_payment_process_time_ms', 100.0, NOW()),
('MET-174000000004', 'baseline_report_generation_time_ms', 500.0, NOW());

-- =================================
-- 14. 性能调优配置
-- =================================

-- 查询缓存和连接优化
SET GLOBAL max_connections = 1000;
SET GLOBAL max_user_connections = 100;
SET GLOBAL connect_timeout = 30;
SET GLOBAL wait_timeout = 28800;
SET GLOBAL interactive_timeout = 28800;

-- 查询优化参数
SET GLOBAL tmp_table_size = 64 * 1024 * 1024; -- 64MB
SET GLOBAL max_heap_table_size = 64 * 1024 * 1024; -- 64MB
SET GLOBAL sort_buffer_size = 2 * 1024 * 1024; -- 2MB
SET GLOBAL read_buffer_size = 1 * 1024 * 1024; -- 1MB

-- InnoDB存储引擎优化
-- SET GLOBAL innodb_buffer_pool_instances = 8; -- 只读变量，需在配置文件中设置
-- SET GLOBAL innodb_buffer_pool_chunk_size = 134217728; -- 只读变量，需在配置文件中设置
SET GLOBAL innodb_log_buffer_size = 16777216; -- 16MB
SET GLOBAL innodb_flush_log_at_trx_commit = 1; -- 数据安全优先
SET GLOBAL innodb_thread_concurrency = 16;
-- SET GLOBAL innodb_read_io_threads = 8; -- 只读变量，需在配置文件中设置
-- SET GLOBAL innodb_write_io_threads = 8; -- 只读变量，需在配置文件中设置
-- SET GLOBAL innodb_file_per_table = ON; -- 可能为只读变量，建议在配置文件中设置
SET GLOBAL innodb_autoextend_increment = 64; -- 64MB自动扩展

-- 慢查询监控配置
SET GLOBAL slow_query_log = ON;
SET GLOBAL long_query_time = 2.0; -- 2秒以上记录慢查询
SET GLOBAL log_queries_not_using_indexes = ON;
SET GLOBAL min_examined_row_limit = 1000;

-- 性能模式配置
UPDATE performance_schema.setup_instruments 
SET ENABLED = 'YES', TIMED = 'YES' 
WHERE NAME LIKE 'statement/sql/%';

UPDATE performance_schema.setup_consumers 
SET ENABLED = 'YES' 
WHERE NAME IN (
    'events_statements_current',
    'events_statements_history',
    'events_statements_summary_by_digest'
);

-- 表缓存配置
SET GLOBAL table_open_cache = 4000;
SET GLOBAL table_definition_cache = 2000;
SET GLOBAL thread_cache_size = 100;
SET GLOBAL key_buffer_size = 16777216; -- 16MB

-- 启用事件调度器
SET GLOBAL event_scheduler = ON;

-- =================================
-- 15. 自动化事件调度
-- =================================

DELIMITER //

-- 每日销售报表自动生成
CREATE EVENT IF NOT EXISTS ev_daily_sales_report
ON SCHEDULE EVERY 1 DAY
STARTS (CURDATE() + INTERVAL 1 DAY + INTERVAL 1 HOUR)
DO
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_store_id CHAR(36);
    DECLARE store_cursor CURSOR FOR 
        SELECT id FROM stores WHERE status = 'ACTIVE' AND is_deleted = FALSE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN store_cursor;
    store_loop: LOOP
        FETCH store_cursor INTO v_store_id;
        IF done THEN
            LEAVE store_loop;
        END IF;
        
        CALL sp_generate_daily_sales_report(v_store_id, CURDATE() - INTERVAL 1 DAY);
    END LOOP;
    CLOSE store_cursor;
END//

-- 每小时库存预警检查
CREATE EVENT IF NOT EXISTS ev_inventory_alert_check
ON SCHEDULE EVERY 1 HOUR
STARTS CURRENT_TIMESTAMP
DO
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_store_id CHAR(36);
    DECLARE v_product_name VARCHAR(200);
    DECLARE v_current_stock INT;
    DECLARE v_min_stock INT;
    DECLARE store_cursor CURSOR FOR 
        SELECT id FROM stores WHERE status = 'ACTIVE' AND is_deleted = FALSE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN store_cursor;
    store_loop: LOOP
        FETCH store_cursor INTO v_store_id;
        IF done THEN
            LEAVE store_loop;
        END IF;
        
        INSERT INTO notifications (store_id, title, message, type, created_by)
        SELECT 
            v_store_id,
            '库存预警',
            CONCAT('商品 "', p.product_name, '" 库存不足，当前库存：', i.current_stock, '，最低库存：', i.min_stock),
            'INVENTORY_ALERT',
            1
        FROM inventory i
        JOIN products p ON i.product_id = p.product_id
        WHERE p.store_id = v_store_id
          AND i.current_stock <= i.min_stock
          AND p.is_active = TRUE
          AND p.is_deleted = FALSE
          AND i.is_deleted = FALSE;
    END LOOP;
    CLOSE store_cursor;
END//

-- 清理过期会话
CREATE EVENT IF NOT EXISTS ev_cleanup_expired_sessions
ON SCHEDULE EVERY 1 HOUR
STARTS CURRENT_TIMESTAMP
DO
BEGIN
    DELETE FROM user_sessions 
    WHERE expires_at < NOW() - INTERVAL 1 DAY;
END//

-- 清理已读通知 (保留30天)
CREATE EVENT IF NOT EXISTS ev_cleanup_old_notifications
ON SCHEDULE EVERY 1 DAY
STARTS TIMESTAMP(CURDATE() + INTERVAL 1 DAY, '04:00:00')
DO
BEGIN
    DELETE FROM notifications 
    WHERE is_read = TRUE 
      AND read_at < NOW() - INTERVAL 30 DAY;
END//

-- 数据库连接数监控
CREATE EVENT IF NOT EXISTS ev_monitor_connections
ON SCHEDULE EVERY 5 MINUTE
STARTS CURRENT_TIMESTAMP
DO
BEGIN
    DECLARE current_connections INT;
    DECLARE max_connections_limit INT;
    
    SELECT VARIABLE_VALUE INTO current_connections 
    FROM performance_schema.global_status 
    WHERE VARIABLE_NAME = 'Threads_connected';
    
    SELECT @@max_connections INTO max_connections_limit;
    
    IF current_connections > max_connections_limit * 0.8 THEN
        INSERT INTO system_alerts (alert_type, alert_level, alert_message, alert_data)
        VALUES (
            'HIGH_CONNECTIONS', 
            'WARNING',
            CONCAT('数据库连接数过高: ', current_connections, '/', max_connections_limit),
            JSON_OBJECT('current_connections', current_connections, 'max_connections', max_connections_limit)
        );
    END IF;
END//

-- 每周统计信息更新
CREATE EVENT IF NOT EXISTS ev_weekly_stats_update
ON SCHEDULE EVERY 1 WEEK
STARTS (CURDATE() + INTERVAL (7 - WEEKDAY(CURDATE())) DAY + INTERVAL 3 HOUR)
DO
BEGIN
    ANALYZE TABLE stores, users, products, categories, inventory, 
                 orders, order_items, payments, customers, coupons;
    
    INSERT INTO performance_metrics (metric_id, metric_name, metric_value, measured_at)
    SELECT 
        CONCAT('MET-', UNIX_TIMESTAMP(NOW()), '-001'),
        'avg_order_processing_time',
        AVG(TIMESTAMPDIFF(SECOND, created_at, updated_at)),
        NOW()
    FROM orders 
    WHERE created_at >= CURDATE() - INTERVAL 7 DAY
      AND status = 'COMPLETED';
      
    INSERT INTO performance_metrics (metric_id, metric_name, metric_value, measured_at)
    SELECT 
        CONCAT('MET-', UNIX_TIMESTAMP(NOW()), '-002'),
        'daily_order_count',
        COUNT(*) / 7.0,
        NOW()
    FROM orders 
    WHERE created_at >= CURDATE() - INTERVAL 7 DAY;
END//

DELIMITER ;

-- =================================
-- 16. 安全和权限优化
-- =================================

-- 创建只读用户 (用于报表查询)
CREATE USER IF NOT EXISTS 'pos_readonly'@'%' IDENTIFIED BY 'ReadOnly@2024!';
GRANT SELECT ON pos_system.* TO 'pos_readonly'@'%';
GRANT SELECT ON performance_schema.* TO 'pos_readonly'@'%';

-- 创建备份用户
CREATE USER IF NOT EXISTS 'pos_backup'@'localhost' IDENTIFIED BY 'Backup@2024!';
GRANT SELECT, LOCK TABLES, SHOW VIEW, EVENT, TRIGGER ON pos_system.* TO 'pos_backup'@'localhost';

-- 创建应用用户 (限制权限)
CREATE USER IF NOT EXISTS 'pos_app'@'%' IDENTIFIED BY 'PosApp@2024!';
GRANT SELECT, INSERT, UPDATE, DELETE ON pos_system.* TO 'pos_app'@'%';
GRANT EXECUTE ON pos_system.* TO 'pos_app'@'%';

-- 完成POS系统数据库初始化
