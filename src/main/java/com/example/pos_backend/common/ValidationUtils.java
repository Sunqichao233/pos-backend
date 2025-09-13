package com.example.pos_backend.common;

import com.example.pos_backend.constants.GlobalConstants;

import java.util.regex.Pattern;

/**
 * 数据验证工具类
 * 提供常见的数据格式验证方法
 */
public class ValidationUtils {

    /**
     * 邮箱正则表达式模式
     */
    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile(GlobalConstants.Regex.EMAIL);

    /**
     * 手机号正则表达式模式
     */
    private static final Pattern PHONE_PATTERN = 
            Pattern.compile(GlobalConstants.Regex.PHONE);

    /**
     * 用户名正则表达式模式
     */
    private static final Pattern USERNAME_PATTERN = 
            Pattern.compile(GlobalConstants.Regex.USERNAME);

    /**
     * 密码正则表达式模式
     */
    private static final Pattern PASSWORD_PATTERN = 
            Pattern.compile(GlobalConstants.Regex.PASSWORD);

    /**
     * 身份证号正则表达式模式（中国大陆）
     */
    private static final Pattern ID_CARD_PATTERN = 
            Pattern.compile("^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");

    /**
     * 银行卡号正则表达式模式
     */
    private static final Pattern BANK_CARD_PATTERN = 
            Pattern.compile("^[0-9]{16,19}$");

    /**
     * IP地址正则表达式模式
     */
    private static final Pattern IP_ADDRESS_PATTERN = 
            Pattern.compile("^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$");

    /**
     * URL正则表达式模式
     */
    private static final Pattern URL_PATTERN = 
            Pattern.compile("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$");

    /**
     * 私有构造函数，防止实例化
     */
    private ValidationUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 验证邮箱格式
     *
     * @param email 邮箱地址
     * @return 是否为有效邮箱格式
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * 验证手机号格式
     *
     * @param phone 手机号
     * @return 是否为有效手机号格式
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * 验证用户名格式
     *
     * @param username 用户名
     * @return 是否为有效用户名格式
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }

    /**
     * 验证密码强度
     *
     * @param password 密码
     * @return 是否为强密码
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * 验证身份证号格式（中国大陆）
     *
     * @param idCard 身份证号
     * @return 是否为有效身份证号格式
     */
    public static boolean isValidIdCard(String idCard) {
        if (idCard == null || idCard.trim().isEmpty()) {
            return false;
        }
        return ID_CARD_PATTERN.matcher(idCard.trim().toUpperCase()).matches();
    }

    /**
     * 验证银行卡号格式
     *
     * @param bankCard 银行卡号
     * @return 是否为有效银行卡号格式
     */
    public static boolean isValidBankCard(String bankCard) {
        if (bankCard == null || bankCard.trim().isEmpty()) {
            return false;
        }
        String cleanCard = bankCard.replaceAll("\\s", "");
        return BANK_CARD_PATTERN.matcher(cleanCard).matches();
    }

    /**
     * 验证IP地址格式
     *
     * @param ipAddress IP地址
     * @return 是否为有效IP地址格式
     */
    public static boolean isValidIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return false;
        }
        return IP_ADDRESS_PATTERN.matcher(ipAddress.trim()).matches();
    }

    /**
     * 验证URL格式
     *
     * @param url URL地址
     * @return 是否为有效URL格式
     */
    public static boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        return URL_PATTERN.matcher(url.trim()).matches();
    }

    /**
     * 验证字符串是否为空或仅包含空白字符
     *
     * @param str 字符串
     * @return 是否为空白
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 验证字符串是否不为空且不仅包含空白字符
     *
     * @param str 字符串
     * @return 是否不为空白
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 验证字符串长度是否在指定范围内
     *
     * @param str       字符串
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return 是否在长度范围内
     */
    public static boolean isLengthBetween(String str, int minLength, int maxLength) {
        if (str == null) {
            return minLength <= 0;
        }
        int length = str.length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * 验证数字是否在指定范围内
     *
     * @param number 数字
     * @param min    最小值
     * @param max    最大值
     * @return 是否在范围内
     */
    public static boolean isNumberBetween(Number number, Number min, Number max) {
        if (number == null) {
            return false;
        }
        double value = number.doubleValue();
        double minValue = min.doubleValue();
        double maxValue = max.doubleValue();
        return value >= minValue && value <= maxValue;
    }

    /**
     * 验证字符串是否只包含数字
     *
     * @param str 字符串
     * @return 是否只包含数字
     */
    public static boolean isNumeric(String str) {
        if (isBlank(str)) {
            return false;
        }
        return str.matches("\\d+");
    }

    /**
     * 验证字符串是否只包含字母
     *
     * @param str 字符串
     * @return 是否只包含字母
     */
    public static boolean isAlpha(String str) {
        if (isBlank(str)) {
            return false;
        }
        return str.matches("[a-zA-Z]+");
    }

    /**
     * 验证字符串是否只包含字母和数字
     *
     * @param str 字符串
     * @return 是否只包含字母和数字
     */
    public static boolean isAlphanumeric(String str) {
        if (isBlank(str)) {
            return false;
        }
        return str.matches("[a-zA-Z0-9]+");
    }

    /**
     * 验证字符串是否包含中文字符
     *
     * @param str 字符串
     * @return 是否包含中文字符
     */
    public static boolean containsChinese(String str) {
        if (isBlank(str)) {
            return false;
        }
        return str.matches(".*[\\u4e00-\\u9fa5].*");
    }

    /**
     * 验证字符串是否只包含中文字符
     *
     * @param str 字符串
     * @return 是否只包含中文字符
     */
    public static boolean isChineseOnly(String str) {
        if (isBlank(str)) {
            return false;
        }
        return str.matches("[\\u4e00-\\u9fa5]+");
    }

    /**
     * 验证年龄是否合法
     *
     * @param age 年龄
     * @return 是否为合法年龄
     */
    public static boolean isValidAge(Integer age) {
        return age != null && age >= 0 && age <= 150;
    }

    /**
     * 验证邮政编码格式（中国）
     *
     * @param postalCode 邮政编码
     * @return 是否为有效邮政编码格式
     */
    public static boolean isValidPostalCode(String postalCode) {
        if (isBlank(postalCode)) {
            return false;
        }
        return postalCode.matches("^[1-9]\\d{5}$");
    }

    /**
     * 验证QQ号格式
     *
     * @param qq QQ号
     * @return 是否为有效QQ号格式
     */
    public static boolean isValidQQ(String qq) {
        if (isBlank(qq)) {
            return false;
        }
        return qq.matches("^[1-9]\\d{4,10}$");
    }

    /**
     * 验证微信号格式
     *
     * @param wechat 微信号
     * @return 是否为有效微信号格式
     */
    public static boolean isValidWechat(String wechat) {
        if (isBlank(wechat)) {
            return false;
        }
        return wechat.matches("^[a-zA-Z][a-zA-Z0-9_-]{5,19}$");
    }
}
