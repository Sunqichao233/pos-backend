package com.example.pos_backend.constants;

import java.util.Arrays;
import java.util.List;

/**
 * 用户业务模块常量定义
 * 专门管理用户相关的业务常量
 */
public class UserConstants {

    /**
     * 用户角色常量
     */
    public static class Role {
        public static final String OWNER = "OWNER";
        public static final String MANAGER = "MANAGER";
        public static final String STAFF = "STAFF";
        
        // 所有有效角色的列表
        public static final List<String> ALL_ROLES = Arrays.asList(OWNER, MANAGER, STAFF);
        
        // 默认角色
        public static final String DEFAULT = STAFF;
        
        /**
         * 检查角色是否有效
         */
        public static boolean isValid(String role) {
            return role != null && ALL_ROLES.contains(role);
        }
        
        /**
         * 获取角色显示名称
         */
        public static String getDisplayName(String role) {
            switch (role) {
                case OWNER: return "店主";
                case MANAGER: return "经理";
                case STAFF: return "员工";
                default: return "未知角色";
            }
        }
    }

    /**
     * 用户状态常量
     */
    public static class Status {
        public static final String ACTIVE = "ACTIVE";
        public static final String INACTIVE = "INACTIVE";
        public static final String SUSPENDED = "SUSPENDED";
        
        // 所有有效状态的列表
        public static final List<String> ALL_STATUSES = Arrays.asList(ACTIVE, INACTIVE, SUSPENDED);
        
        // 默认状态
        public static final String DEFAULT = ACTIVE;
        
        /**
         * 检查状态是否有效
         */
        public static boolean isValid(String status) {
            return status != null && ALL_STATUSES.contains(status);
        }
        
        /**
         * 获取状态显示名称
         */
        public static String getDisplayName(String status) {
            switch (status) {
                case ACTIVE: return "激活";
                case INACTIVE: return "未激活";
                case SUSPENDED: return "暂停";
                default: return "未知状态";
            }
        }
    }

    /**
     * 用户操作相关常量
     */
    public static class Operation {
        public static final String LOGIN = "LOGIN";
        public static final String LOGOUT = "LOGOUT";
        public static final String PASSWORD_CHANGE = "PASSWORD_CHANGE";
        public static final String PIN_CHANGE = "PIN_CHANGE";
        public static final String PROFILE_UPDATE = "PROFILE_UPDATE";
    }

    /**
     * 用户查询相关常量
     */
    public static class Query {
        public static final String FIND_BY_USERNAME = "findByUsername";
        public static final String FIND_BY_EMAIL = "findByEmail";
        public static final String FIND_BY_ORG_ID = "findByOrgId";
        public static final String FIND_BY_ROLE = "findByRole";
        public static final String FIND_BY_STATUS = "findByStatus";
    }

    /**
     * 用户验证相关常量
     */
    public static class Validation {
        public static final int MIN_USERNAME_LENGTH = 3;
        public static final int MAX_USERNAME_LENGTH = GlobalConstants.StringLength.USERNAME;
        public static final int MIN_PASSWORD_LENGTH = 8;
        public static final int MAX_PASSWORD_LENGTH = GlobalConstants.StringLength.PASSWORD;
        public static final String USERNAME_PATTERN = GlobalConstants.Regex.USERNAME;
        public static final String EMAIL_PATTERN = GlobalConstants.Regex.EMAIL;
    }

    /**
     * 私有构造函数，防止实例化
     */
    private UserConstants() {
        throw new IllegalStateException("Utility class");
    }
}
