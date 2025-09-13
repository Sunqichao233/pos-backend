package com.example.pos_backend.constants;

/**
 * 全局通用常量
 * 包含系统级别的通用常量定义
 */
public class GlobalConstants {

    /**
     * 通用状态常量
     */
    public static class CommonStatus {
        public static final String ACTIVE = "ACTIVE";
        public static final String INACTIVE = "INACTIVE";
        public static final String DELETED = "DELETED";
        public static final String PENDING = "PENDING";
        public static final String COMPLETED = "COMPLETED";
        public static final String FAILED = "FAILED";
        public static final String CANCELLED = "CANCELLED";
    }

    /**
     * 数据库相关常量
     */
    public static class Database {
        public static final Boolean NOT_DELETED = false;
        public static final Boolean IS_DELETED = true;
        public static final int DEFAULT_PAGE_SIZE = 20;
        public static final int MAX_PAGE_SIZE = 100;
        public static final String DEFAULT_SORT_FIELD = "createdAt";
        public static final String DESC = "desc";
        public static final String ASC = "asc";
    }

    /**
     * 字符串长度限制常量
     */
    public static class StringLength {
        public static final int SHORT_TEXT = 50;
        public static final int MEDIUM_TEXT = 100;
        public static final int LONG_TEXT = 255;
        public static final int DESCRIPTION = 500;
        public static final int CONTENT = 2000;
        
        // 特定字段长度
        public static final int USERNAME = 50;
        public static final int EMAIL = 100;
        public static final int PASSWORD = 255;
        public static final int PHONE = 20;
        public static final int NAME = 50;
    }

    /**
     * HTTP 响应消息常量
     */
    public static class ResponseMessage {
        public static final String SUCCESS = "操作成功";
        public static final String CREATED = "创建成功";
        public static final String UPDATED = "更新成功";
        public static final String DELETED = "删除成功";
        public static final String NOT_FOUND = "资源不存在";
        public static final String BAD_REQUEST = "请求参数错误";
        public static final String UNAUTHORIZED = "未授权访问";
        public static final String FORBIDDEN = "权限不足";
        public static final String INTERNAL_ERROR = "系统内部错误";
        public static final String VALIDATION_ERROR = "数据验证失败";
    }

    /**
     * 日期时间格式常量
     */
    public static class DateTimeFormat {
        public static final String DATE = "yyyy-MM-dd";
        public static final String DATETIME = "yyyy-MM-dd HH:mm:ss";
        public static final String TIMESTAMP = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        public static final String TIME = "HH:mm:ss";
    }

    /**
     * 正则表达式常量
     */
    public static class Regex {
        public static final String EMAIL = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
        public static final String PHONE = "^[0-9]{10,15}$";
        public static final String USERNAME = "^[a-zA-Z0-9_]{3,50}$";
        public static final String PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$";
    }

    /**
     * 私有构造函数，防止实例化
     */
    private GlobalConstants() {
        throw new IllegalStateException("Utility class");
    }
}
