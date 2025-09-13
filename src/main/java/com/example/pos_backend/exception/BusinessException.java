package com.example.pos_backend.exception;

/**
 * 通用业务异常
 * 用于处理业务逻辑中的各种异常情况
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误代码
     */
    private String errorCode;

    /**
     * 默认构造函数
     */
    public BusinessException() {
        super("业务处理异常");
    }

    /**
     * 带消息的构造函数
     *
     * @param message 异常消息
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * 带消息和原因的构造函数
     *
     * @param message 异常消息
     * @param cause   异常原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 带错误代码和消息的构造函数
     *
     * @param errorCode 错误代码
     * @param message   异常消息
     */
    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 带错误代码、消息和原因的构造函数
     *
     * @param errorCode 错误代码
     * @param message   异常消息
     * @param cause     异常原因
     */
    public BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * 获取错误代码
     *
     * @return 错误代码
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 设置错误代码
     *
     * @param errorCode 错误代码
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * 创建数据验证异常
     *
     * @param message 异常消息
     * @return BusinessException 实例
     */
    public static BusinessException dataValidation(String message) {
        return new BusinessException("DATA_VALIDATION_ERROR", message);
    }

    /**
     * 创建数据重复异常
     *
     * @param message 异常消息
     * @return BusinessException 实例
     */
    public static BusinessException dataDuplicate(String message) {
        return new BusinessException("DATA_DUPLICATE_ERROR", message);
    }

    /**
     * 创建数据不存在异常
     *
     * @param message 异常消息
     * @return BusinessException 实例
     */
    public static BusinessException dataNotFound(String message) {
        return new BusinessException("DATA_NOT_FOUND", message);
    }

    /**
     * 创建操作不允许异常
     *
     * @param message 异常消息
     * @return BusinessException 实例
     */
    public static BusinessException operationNotAllowed(String message) {
        return new BusinessException("OPERATION_NOT_ALLOWED", message);
    }

    /**
     * 创建状态错误异常
     *
     * @param message 异常消息
     * @return BusinessException 实例
     */
    public static BusinessException statusError(String message) {
        return new BusinessException("STATUS_ERROR", message);
    }

    /**
     * 创建权限不足异常
     *
     * @param message 异常消息
     * @return BusinessException 实例
     */
    public static BusinessException insufficientPermission(String message) {
        return new BusinessException("INSUFFICIENT_PERMISSION", message);
    }
}
