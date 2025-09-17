# POS系统数据库E-R图设计

## 设计概述

基于Dashboard后台管理需求，采用Monty风格的数据库设计理念，以数据完整性为核心，兼顾查询性能的多租户POS系统数据模型。

## E-R图

```
                    POS System Database E-R Diagram (Square风格)

┌─────────────────────────────────────────────────────────────────────────────┐
│                          商家与门店管理（Square风格）                       │
└─────────────────────────────────────────────────────────────────────────────┘

   Merchant (商家)                  MerchantBankAccount (银行账户)    Store (门店)
┌─────────────────┐              ┌─────────────────┐              ┌─────────────────┐
│ id (PK/UUID)    │─────1:N─────│ id (PK/UUID)    │              │ id (PK/UUID)    │
│ email           │              │ merchant_id(FK) │              │ merchant_id(FK) │
│ password_hash   │              │ account_number  │              │ store_name      │
│ business_name   │              │ routing_number  │              │ address         │
│ industry        │              │ account_holder  │              │ timezone        │
│ currency        │              │ account_type    │              │ status          │
│ country         │              │ bank_name       │              │ tax_rate        │
│ status          │              │ is_primary      │              │ currency        │
│ created_at      │              │ is_verified     │              │ business_hours  │
│ updated_at      │              │ status          │              │ created_at      │
│ created_by      │              │ created_at      │              │ updated_at      │
│ updated_by      │              │ updated_at      │              │ created_by      │
│ is_deleted      │              │ created_by      │              │ updated_by      │
└─────────────────┘              │ updated_by      │              │ is_deleted      │
                                 │ is_deleted      │              └─────────────────┘
                                 └─────────────────┘                       │
                                                                          │
                                                                         1:N
                                                                          │
                                                                          ▼
                                    User (员工)                   Role (角色)
                                ┌─────────────────┐           ┌─────────────────┐
                                │ user_id(PK/UUID)│──N:M─────│ role_id(UUID)   │
                                │ merchant_id(FK) │           │ role_name       │
                                │ store_id (FK)   │           │ role_code       │
                                │ username        │           │ description     │
                                │ email           │           │ is_active       │
                                │ password_hash   │           │ created_at      │
                                │ pin_hash        │           │ updated_at      │
                                │ first_name      │           │ created_by      │
                                │ last_name       │           │ updated_by      │
                                │ role            │           │ is_deleted      │
                                │ status          │           └─────────────────┘
                                │ salary          │                    │
                                │ hire_date       │                    │
                                │ last_login_at   │           Permission (权限) │
                                │ created_at      │           ┌─────────────────┐│
                                │ updated_at      │           │ permission_id(UUID)││
                                │ created_by(UUID)│           │ permission_name │ │
                                │ updated_by(UUID)│           │ permission_code │ │
                                │ is_deleted      │           │ resource        │ │
                                └─────────────────┘           │ action          │ │
                                         │                    │ description     │ │
                                         │                    │ created_at      │ │
                                         │                    │ updated_at      │ │
                                         │                    │ created_by(UUID)│ │
                                         │                    │ updated_by(UUID)│ │
                                         │                    │ is_deleted      │ │
                                         │                    └─────────────────┘ │
                                         │                                       │
    UserRole (用户角色关联)               │              RolePermission (角色权限关联) │
┌─────────────────┐                     │              ┌─────────────────┐          │
│ user_id(FK/UUID)│─────────────────────┘              │ role_id(FK/UUID)│          │
│ role_id(FK/UUID)│                                    │permission_id(FK/UUID)│──────────┘
│ assigned_at     │                                    │ granted_at      │
│ assigned_by(UUID)│                                   │ granted_by(UUID)│
│ is_active       │                                    └─────────────────┘
└─────────────────┘

    UserSession (用户会话)               Attendance (考勤记录)          Notification (通知)
┌─────────────────┐                    ┌─────────────────┐           ┌─────────────────┐
│ session_id(UUID)│                    │attendance_id(UUID)│         │notification_id  │
│ user_id(FK/UUID)│                    │ user_id(FK/UUID)│           │ (PK/UUID)       │
│ device_id(UUID) │                    │ store_id(FK/UUID)│          │ store_id(FK/UUID)│
│ access_token    │                    │ clock_in_time   │           │ user_id(FK/UUID)│
│ refresh_token   │                    │ clock_out_time  │           │ title           │
│access_token_expires_at│              │ total_hours     │           │ message         │
│refresh_token_expires_at│             │ idempotency_key │           │ type            │
│ ip_address      │                    │ sync_status     │           │ is_read         │
│ user_agent      │                    │ created_at      │           │ created_at      │
│ status          │                    │ updated_at      │           │ updated_at      │
│ last_activity_at│                    │ created_by(UUID)│           │ created_by(UUID)│
│ created_at      │                    │ updated_by(UUID)│           │ updated_by(UUID)│
│ created_by(UUID)│                    │ is_deleted      │           │ read_at         │
│ updated_at      │                    └─────────────────┘           │ is_deleted      │
│ updated_by(UUID)│                                                  └─────────────────┘
│ is_deleted      │
└─────────────────┘

    MerchantSession (商家会话)
┌─────────────────┐
│ session_id(UUID)│
│merchant_id(FK/UUID)│
│ device_id(UUID) │
│ access_token    │
│ refresh_token   │
│access_token_expires_at│
│refresh_token_expires_at│
│ ip_address      │
│ user_agent      │
│ status          │
│ last_activity_at│
│ created_at      │
│ created_by(UUID)│
│ updated_at      │
│ updated_by(UUID)│
│ is_deleted      │
└─────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                              商品与库存管理                                 │
└─────────────────────────────────────────────────────────────────────────────┘

    Category (分类)                Product (商品)               Inventory (库存)
┌─────────────────┐              ┌─────────────────┐           ┌─────────────────┐
│category_id(UUID)│─────1:N─────│product_id(UUID) │──1:1─────│inventory_id(UUID)│
│ store_id(FK/UUID)│             │merchant_id(FK/UUID)│        │product_id(FK/UUID)│
│ category_name   │              │ store_id(FK/UUID)│          │ current_stock   │
│ description     │              │category_id(FK/UUID)│        │ min_stock       │
│ display_order   │              │ product_name    │           │ max_stock       │
│ is_active       │              │ description     │           │ cost_price      │
│ created_at      │              │ price           │           │ last_updated    │
│ updated_at      │              │ image_url       │           │ created_at      │
│created_by(UUID) │              │ is_active       │           │ updated_at      │
│updated_by(UUID) │              │ created_at      │           │created_by(UUID) │
│ is_deleted      │              │ updated_at      │           │updated_by(UUID) │
└─────────────────┘              │created_by(UUID) │           │ is_deleted      │
                                 │updated_by(UUID) │           └─────────────────┘
                                 │ is_deleted      │
                                 └─────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                              订单与支付管理                                 │
└─────────────────────────────────────────────────────────────────────────────┘

    Customer (客户)                Order (订单)                 OrderItem (订单项)
┌─────────────────┐              ┌─────────────────┐           ┌─────────────────┐
│customer_id(UUID)│─────1:N─────│ order_id(UUID)  │──1:N─────│order_item_id(UUID)│
│store_id(FK/UUID)│              │merchant_id(FK/UUID)│        │merchant_id(FK/UUID)│
│ customer_name   │              │store_id(FK/UUID)│           │store_id(FK/UUID)│
│ phone           │              │customer_id(FK/UUID)│        │order_id(FK/UUID)│
│ email           │              │user_id(FK/UUID) │           │product_id(FK/UUID)│
│ points_balance  │              │ order_number    │           │ quantity        │
│ membership_level│              │ idempotency_key │           │ unit_price      │
│ created_at      │              │ total_amount    │           │ subtotal        │
│ updated_at      │              │ tax_amount      │           │ created_at      │
│created_by(UUID) │              │ tip_amount      │           │ updated_at      │
│updated_by(UUID) │              │ discount_amount │           │created_by(UUID) │
│ is_deleted      │              │ payment_status  │           │updated_by(UUID) │
└─────────────────┘              │ order_status    │           │ is_deleted      │
                                 │ order_type      │           └─────────────────┘
    Coupon (优惠券)               │ created_at      │
┌─────────────────┐              │ updated_at      │           Payment (支付)
│coupon_id(UUID)  │──N:M────────│created_by(UUID) │           ┌─────────────────┐
│store_id(FK/UUID)│              │updated_by(UUID) │──1:N─────│payment_id(UUID) │
│ coupon_code     │              │ completed_at    │           │order_id(FK/UUID)│
│ discount_type   │              │ is_deleted      │           │ idempotency_key │
│ discount_value  │              └─────────────────┘           │ payment_method  │
│ min_order_amount│                                            │ amount          │
│ valid_from      │              OrderCoupon (关联表)          │ transaction_id  │
│ valid_until     │              ┌─────────────────┐           │ status          │
│ usage_limit     │              │order_id(FK/UUID)│           │ processed_at    │
│ used_count      │              │coupon_id(FK/UUID)│          │ created_at      │
│ is_active       │              │ discount_applied│           │ updated_at      │
│ created_at      │              │ created_at      │           │created_by(UUID) │
│ updated_at      │              │created_by(UUID) │           │updated_by(UUID) │
│created_by(UUID) │              └─────────────────┘           │ is_deleted      │
│updated_by(UUID) │                                            └─────────────────┘
│ is_deleted      │
└─────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                              考勤与会话管理                                 │
└─────────────────────────────────────────────────────────────────────────────┘

    Attendance (考勤)              UserSession (用户会话)       Closing (交班记录)
┌─────────────────┐              ┌─────────────────┐           ┌─────────────────┐
│attendance_id(UUID)│            │session_id(UUID) │           │closing_id(UUID) │
│user_id(FK/UUID) │              │user_id(FK/UUID) │           │user_id(FK/UUID) │
│store_id(FK/UUID)│              │device_id(FK/UUID)│          │store_id(FK/UUID)│
│ clock_in_time   │              │ access_token    │           │ closing_date    │
│ clock_out_time  │              │ refresh_token   │           │ cash_counted    │
│ total_hours     │              │access_token_expires_at│     │ cash_expected   │
│ idempotency_key │              │refresh_token_expires_at│    │ difference      │
│ sync_status     │              │ ip_address      │           │ sync_status     │
│ created_at      │              │ user_agent      │           │ created_at      │
│ updated_at      │              │ status          │           │ updated_at      │
│ created_by      │              │ last_activity_at│           │ created_by      │
│ updated_by      │              │ created_at      │           │ updated_by      │
│ is_deleted      │              │ created_by(UUID)│           │ is_deleted      │
└─────────────────┘              │ updated_at      │           └─────────────────┘
                                 │ updated_by(UUID)│
                                 │ is_deleted      │
                                 └─────────────────┘
                                
                                MerchantSession (商家会话)     Receipt (收据记录)
                                ┌─────────────────┐           ┌─────────────────┐
                                │ session_id(UUID)│           │ receipt_id (PK) │
                                │merchant_id(FK/UUID)│        │ order_id (FK)   │
                                │ device_id(UUID) │           │ delivery_method │
                                │ access_token    │           │ recipient       │
                                │ refresh_token   │           │ sent_at         │
                                │access_token_expires_at│     │ status          │
                                │refresh_token_expires_at│    │ created_at      │
                                │ ip_address      │           │ updated_at      │
                                │ user_agent      │           │ created_by      │
                                │ status          │           │ updated_by      │
                                │ last_activity_at│           │ is_deleted      │
                                │ created_at      │           └─────────────────┘
                                │ created_by(UUID)│
                                │ updated_at      │
                                │ updated_by(UUID)│
                                │ is_deleted      │
                                └─────────────────┘
                                                              │ order_id (FK)   │
                                                              │ delivery_method │
                                                              │ recipient       │
                                                              │ sent_at         │
                                                              │ status          │
                                                              │ created_at      │
                                                              │ updated_at      │
                                                              │ created_by      │
                                                              │ updated_by      │
                                                              │ is_deleted      │
                                                              └─────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                              系统管理与配置                                 │
└─────────────────────────────────────────────────────────────────────────────┘

    Device (设备)                  DeviceCode (激活码)          TaxRule (税务规则)
┌─────────────────┐              ┌─────────────────┐           ┌─────────────────┐
│device_id(UUID)  │─────1:N─────│device_code_id(UUID)│        │tax_rule_id(UUID)│
│store_id(FK/UUID)│              │ device_code(12位)│          │store_id(FK/UUID)│
│ device_name     │              │device_id(FK/UUID)│         │ tax_name        │
│ device_type     │              │device_fingerprint│          │ tax_rate        │
│ mac_address     │              │ status(VARCHAR) │           │ tax_type        │
│ ip_address      │              │activation_attempts│         │ applicable_to   │
│ last_online     │              │ max_attempts    │           │ effective_from  │
│ status          │              │ issued_at       │           │ effective_until │
│ registered_at   │              │ expired_at      │           │ is_active       │
│ created_at      │              │ bound_at        │           │ created_at      │
│ updated_at      │              │ created_at      │           │ updated_at      │
│created_by(UUID) │              │ updated_at      │           │created_by(UUID) │
│updated_by(UUID) │              │created_by(UUID) │           │updated_by(UUID) │
│ is_deleted      │              │updated_by(UUID) │           │ is_deleted      │
└─────────────────┘              │ is_deleted      │           └─────────────────┘
                                └─────────────────┘
                                Notification (通知)
                                ┌─────────────────┐
                                │notification_id  │
                                │ (PK)            │
                                │ store_id (FK)   │
                                │ user_id (FK)    │
                                │ title           │
                                │ message         │
                                │ type            │
                                │ is_read         │
                                │ created_at      │
                                │ updated_at      │
                                │ created_by      │
                                │ updated_by      │
                                │ read_at         │
                                │ is_deleted      │
                                └─────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                              报表与汇总表(反范式化)                         │
└─────────────────────────────────────────────────────────────────────────────┘

    DailySalesReport (日销售汇总)
┌─────────────────────────────────────────────────────────────────┐
│ report_id(UUID)         │ 用于快速生成Dashboard报表            │
│ store_id(FK/UUID)       │ 避免复杂的实时统计查询               │
│ report_date             │ 每日定时任务更新                     │
│ total_sales_amount      │                                      │
│ total_orders            │                                      │
│ average_order_value     │                                      │
│ total_tips              │                                      │
│ top_product_id(FK/UUID) │                                      │
│ created_at              │                                      │
│ updated_at              │                                      │
│ created_by(UUID)        │                                      │
│ updated_by(UUID)        │                                      │
│ is_deleted              │                                      │
└─────────────────────────────────────────────────────────────────┘
```

## Dashboard功能实现映射

基于Dashboard后台管理需求，以下是各功能模块与数据库表的对应关系：

### 🔐 商家注册/登录页（Square风格，UUID主键）
- **商家注册**: `Merchant`表 - email、password_hash、business_name、industry等字段（UUID主键：MRC-xxx）
- **商家登录**: `Merchant`表 - email、password_hash字段验证
- **银行账户管理**: `MerchantBankAccount`表 - 独立管理多个银行账户
  - account_number、routing_number、account_holder字段
  - is_primary字段标识主账户
  - is_verified字段标识验证状态
  - 支持多账户管理（CHECKING/SAVINGS）
- **员工登录**: `User`表 - email、password_hash字段（UUID主键：USR-xxx）
- **PIN码登录**: `User`表 - pin_hash字段（iPad设备登录）
- **门店管理**: `Store`表 - 支持多门店（UUID主键：LOC-xxx）
- **忘记密码**: 可扩展重置令牌字段

### 📊 首页（概览 Dashboard）
- **昨日/本周销售额**: `DailySalesReport`表 - total_sales_amount字段汇总
- **客单价**: `DailySalesReport`表 - average_order_value字段
- **小费统计**: `DailySalesReport`表 - total_tips字段
- **热门商品**: `DailySalesReport`表 - top_product_id关联Product表
- **报表过滤**: 通过store_id和report_date筛选

### 🏪 店铺管理页
- **营业时间**: `Store`表 - business_hours字段(JSON格式)
- **地址信息**: `Store`表 - address字段
- **税率设置**: `Store`表 - tax_rate字段
- **币种设置**: `Store`表 - currency字段
- **时区设置**: `Store`表 - timezone字段

### 🍽️ 菜单管理页
- **分类管理**: `Category`表 - category_name、display_order、is_active
- **菜品管理**: `Product`表 - product_name、description、price、image_url
- **价格设置**: `Product`表 - price字段
- **库存管理**: `Inventory`表 - current_stock、min_stock、max_stock
- **图片上传**: `Product`表 - image_url字段存储图片路径
- **库存不足提醒**: 通过Inventory表的min_stock阈值判断

### 👥 员工管理页
- **员工列表**: `User`表 - 基本信息展示
- **权限管理**: `Role`、`Permission`、`UserRole`、`RolePermission`表组合
- **薪资管理**: `User`表 - salary字段
- **打卡记录**: 可扩展`Attendance`表(未在当前设计中)

### 📈 报表页
- **销售额趋势**: `Order`表按时间维度统计total_amount
- **品类销量**: `OrderItem`关联`Product`和`Category`统计
- **毛利分析**: `OrderItem`的unit_price与`Inventory`的cost_price计算
- **自定义时间范围**: 通过Order表的created_at字段筛选

### 👤 客户/会员管理页
- **客户列表**: `Customer`表 - 基本信息管理
- **积分系统**: `Customer`表 - points_balance字段
- **优惠券管理**: `Coupon`表 - 优惠券创建和管理
- **礼品卡**: 可扩展`GiftCard`表(未在当前设计中)
- **发放优惠券**: 通过Coupon表创建，OrderCoupon表记录使用

### 💰 支付与结算页
- **支付记录**: `Payment`表 - 记录所有支付信息
- **结算账户**: 可扩展`SettlementAccount`表(未在当前设计中)
- **结算周期**: 通过Payment表的processed_at字段按周期统计
- **手续费明细**: Payment表可扩展fee_amount字段
- **对账功能**: 利用Payment表的transaction_id和idempotency_key

### 📱 设备管理页
- **POS终端列表**: `Device`表 - 设备信息管理
- **设备状态**: `Device`表 - status、last_online字段
- **设备绑定**: `Device`表 - mac_address、ip_address字段
- **设备激活**: `DeviceCode`表 - Square风格激活码管理，6-8位短码
- **激活码状态**: `DeviceCode`表 - status字段(UNUSED/BOUND/EXPIRED)
- **设备指纹**: `DeviceCode`表 - device_fingerprint字段存储设备唯一标识
- **激活尝试**: `DeviceCode`表 - activation_attempts字段记录尝试次数，最大3次
- **即时激活**: 用户输入激活码自助绑定设备，一设备一有效码
- **设备移除**: 通过is_deleted软删除

### 🧾 税务设置页
- **税务规则**: `TaxRule`表 - 税率和适用规则管理
- **州税/市税**: `TaxRule`表 - tax_type字段区分
- **生效时间**: `TaxRule`表 - effective_from、effective_until字段

### 🔔 通知/消息中心页
- **系统公告**: `Notification`表 - type字段标识公告类型
- **设备异常提醒**: `Notification`表 - 关联Device表异常状态
- **消息状态**: `Notification`表 - is_read、read_at字段
- **消息详情**: `Notification`表 - title、message字段

### 🔒 权限系统实现
- **角色定义**: `Role`表 - role_name、role_code
- **权限定义**: `Permission`表 - resource、action精确控制
- **用户角色**: `UserRole`表 - 多对多关系实现
- **角色权限**: `RolePermission`表 - 灵活权限组合
- **权限验证**: 通过用户→角色→权限的链式查询

### 🛡️ 数据安全与审计
- **操作审计**: 所有表的created_by、updated_by字段
- **数据保护**: 所有表的is_deleted软删除
- **幂等性**: Order、Payment表的idempotency_key防重复
- **数据追踪**: 统一的时间戳字段created_at、updated_at

## iPad POS API 数据库支持分析

基于iPad POS App的API需求，以下是当前数据库设计的支持情况和改进建议：

### ✅ **完全支持的API功能**

#### 3. 商品/菜单
- **GET /products**: `Product`表完整支持
- **GET /categories**: `Category`表完整支持  
- **离线缓存**: `updated_at`字段支持增量同步
- **库存管理**: `Inventory`表支持库存查询

#### 4. 购物车/订单
- **POST /orders**: `Order`表有`idempotency_key`防重复
- **PUT /orders/{id}**: `OrderItem`表支持订单更新
- **GET /orders/{id}**: 完整的订单查询支持
- **离线同步**: 幂等性设计支持批量上传

#### 5. 支付
- **POST /payments**: `Payment`表有`idempotency_key`
- **GET /payments/{id}**: 支付状态查询支持
- **POST /refunds**: 通过Payment状态管理退款

#### 7. 历史订单
- **GET /orders?date=...**: `Order`表`created_at`索引支持

#### 8. 日结/交班 (部分支持)
- **GET /reports/daily-sales**: `DailySalesReport`表支持
- **POST /closing**: ✅ 新增`Closing`表支持

#### 9. 设置/硬件
- **GET /settings**: `Store`表支持门店配置
- **POST /devices/register**: `Device`表完整支持

### 🟡 **需要字段扩展的功能**

#### 1. 登录/用户
- **POST /auth/login**: ✅ 需要在`User`表添加`pin_hash`字段
- **GET /auth/me**: ✅ 新增`UserSession`表支持多设备登录
- **离线登录**: UserSession表支持本地缓存

#### 6. 收据/打印
- **POST /receipts**: ✅ 新增`Receipt`表记录收据发送

### 🆕 **需要新增的表**

#### 2. 打卡/考勤
- **POST /attendance/clock-in**: ✅ 新增`Attendance`表
- **POST /attendance/clock-out**: Attendance表支持
- **离线打卡**: `sync_status`字段支持离线同步

### 📋 **新增表设计要点**

#### Attendance (考勤表)
- **idempotency_key**: 防止重复打卡上传
- **sync_status**: 支持离线打卡同步
- **clock_in_time/clock_out_time**: 精确记录打卡时间

#### UserSession (用户会话表)  
- **access_token**: 访问令牌，支持API认证
- **refresh_token**: 刷新令牌，支持令牌续期
- **access_token_expires_at**: 访问令牌过期时间管理
- **refresh_token_expires_at**: 刷新令牌过期时间管理
- **device_id**: 关联具体设备，支持多设备登录
- **ip_address**: 记录登录IP地址，安全审计
- **user_agent**: 记录设备信息，便于管理
- **status**: 会话状态管理(ACTIVE/INACTIVE)

#### MerchantSession (商家会话表)
- **merchant_id**: 外键指向merchants表，商家会话管理
- **access_token**: 商家访问令牌，支持Dashboard API认证
- **refresh_token**: 商家刷新令牌，支持令牌续期
- **access_token_expires_at**: 商家访问令牌过期时间管理
- **refresh_token_expires_at**: 商家刷新令牌过期时间管理
- **device_id**: 关联具体设备，支持多设备登录
- **ip_address**: 记录商家登录IP地址，安全审计
- **user_agent**: 记录设备信息，便于管理
- **status**: 商家会话状态管理(ACTIVE/INACTIVE)

#### Closing (交班记录表)
- **cash_counted/cash_expected**: 现金盘点
- **difference**: 差额记录
- **sync_status**: 支持离线交班

#### Receipt (收据记录表)
- **delivery_method**: print/sms/email
- **recipient**: 收据接收方
- **status**: 发送状态跟踪

### 🔒 **关键设计原则**

1. **幂等性优先**: 所有关键操作表都有`idempotency_key`
2. **离线同步**: 新增`sync_status`字段管理同步状态  
3. **数据完整性**: 保持现有审计字段标准
4. **向后兼容**: 新增表不影响现有Dashboard功能

## 实体关系说明

### 核心实体

#### 1. 商家与门店管理模块（Square风格，UUID主键）
- **Merchant (商家)**: Square风格的商家主体，使用UUID主键（MRC-xxx格式），包含企业基本信息，支持多门店管理
- **MerchantBankAccount (银行账户)**: 独立的银行账户管理表，支持多账户绑定，包含验证状态和主账户标识
- **Store (门店)**: 门店实体，使用UUID主键（LOC-xxx格式），隶属于商家，所有业务数据都以门店为隔离单位
- **User (用户/员工)**: 门店员工，使用UUID主键（USR-xxx格式），通过merchant_id和store_id实现多层级租户隔离
- **Role (角色)**: 权限角色定义，支持灵活的权限管理，使用role_code便于程序判断
- **Permission (权限)**: 细粒度权限定义，通过resource和action实现精确权限控制
- **UserRole (用户角色关联)**: 多对多关系，支持一个用户拥有多个角色
- **RolePermission (角色权限关联)**: 多对多关系，实现角色与权限的灵活组合

#### 2. 商品与库存管理模块
- **Category (商品分类)**: 商品分类管理，支持层级结构
- **Product (商品)**: 商品主表，包含基本信息和价格
- **Inventory (库存)**: 商品库存管理，与商品一对一关系，独立管理库存数据

#### 3. 订单与支付管理模块
- **Customer (客户)**: 客户信息管理，支持会员积分
- **Order (订单)**: 订单主表，记录订单基本信息和状态
- **OrderItem (订单项)**: 订单明细，记录每个商品的购买信息
- **Payment (支付)**: 支付记录，支持多种支付方式和分期支付
- **Coupon (优惠券)**: 优惠券管理
- **OrderCoupon (订单优惠券关联)**: 记录订单使用的优惠券

#### 4. 系统管理与配置模块
- **Device (设备)**: POS终端设备管理
- **DeviceCode (激活码)**: Square风格设备激活码管理，支持6-8位短码即时激活
- **TaxRule (税务规则)**: 税务配置管理，支持复杂税务场景
- **Notification (通知)**: 系统通知消息管理

#### 5. 报表与汇总模块
- **DailySalesReport (日销售汇总)**: 反范式化设计，提升报表查询性能

### 关键关系

#### 一对多关系 (1:N)
- Merchant → MerchantBankAccount: 一个商家可以有多个银行账户
- Merchant → Store: 一个商家可以有多个门店
- Merchant → MerchantSession: 一个商家可以有多个会话（多设备登录）
- Store → User: 一个店铺有多个员工
- Store → Product: 一个店铺有多个商品
- Store → Customer: 一个店铺有多个客户
- User → UserSession: 一个员工可以有多个会话（多设备登录）
- Category → Product: 一个分类包含多个商品
- Customer → Order: 一个客户可以有多个订单
- Order → OrderItem: 一个订单包含多个商品项
- Order → Payment: 一个订单可能有多次支付记录

#### 一对一关系 (1:1)
- Product ↔ Inventory: 商品与库存一对一关系

#### 多对多关系 (N:M)
- User ↔ Role: 通过UserRole表实现，用户可以有多个角色
- Role ↔ Permission: 通过RolePermission表实现，角色可以有多个权限
- Order ↔ Coupon: 通过OrderCoupon表实现，订单可以使用多张优惠券

### 设计特点

#### 1. 多租户架构（Square风格）
- **两层租户隔离**: Merchant层（商家级别）和Store层（门店级别）
- 所有业务表都包含org_id（商家ID）和store_id（门店ID）字段，实现双层数据隔离
- 商家可以管理多个门店，每个门店的数据完全隔离
- 通过应用层和数据库约束确保租户间数据安全

#### 2. 数据完整性保障
- 使用自增主键确保唯一性
- 合理设置外键约束保证引用完整性
- 财务相关字段使用DECIMAL类型避免精度问题

#### 3. 性能优化考虑
- 创建DailySalesReport汇总表，避免复杂的实时统计查询
- 为高频查询字段设计复合索引
- 考虑订单表按时间分区提升查询效率

#### 4. 扩展性设计
- 预留状态字段支持业务流程扩展
- 使用JSON字段存储灵活配置信息
- 软删除设计保护重要历史数据

#### 5. 审计追踪与数据安全
- **统一审计字段**: 所有表包含created_at、updated_at、created_by、updated_by时间戳和操作人员信息
- **软删除设计**: 使用is_deleted字段保护重要历史数据，避免物理删除
- **幂等性保障**: Order和Payment表包含idempotency_key字段，防止重复提交和回调处理
- **数据变更追踪**: 支持完整的数据变更历史记录

#### 6. 权限系统设计
- **细粒度权限控制**: Permission表通过resource和action字段实现精确的权限定义
- **灵活的角色管理**: Role-Permission多对多关系，支持权限的灵活组合
- **权限继承**: 用户通过UserRole关联获得角色权限，支持多角色并存
- **权限审计**: 权限分配和撤销都有完整的操作记录

#### 7. 事务安全设计
- **支付幂等性**: Payment表的idempotency_key确保支付操作的幂等性
- **订单一致性**: Order表的idempotency_key防止重复下单
- **状态管理**: 订单和支付状态的严格控制，确保业务流程的正确性
- **回滚支持**: 软删除设计支持业务操作的安全回滚
