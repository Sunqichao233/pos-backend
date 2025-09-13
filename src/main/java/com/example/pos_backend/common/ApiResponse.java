package com.example.pos_backend.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 统一 API 响应包装类
 * 用于包装所有 API 响应数据，提供统一的响应格式
 *
 * @param <T> 响应数据类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * 响应状态码
     */
    private Integer code;

    /**
     * 操作是否成功
     */
    private Boolean success;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应时间戳
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant timestamp;

    /**
     * 请求追踪ID（可选）
     */
    private String traceId;

    /**
     * 创建成功响应
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .success(true)
                .message("操作成功")
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * 创建成功响应（带自定义消息）
     *
     * @param data    响应数据
     * @param message 自定义消息
     * @param <T>     数据类型
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .code(200)
                .success(true)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * 创建成功响应（仅消息，无数据）
     *
     * @param message 响应消息
     * @param <T>     数据类型
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .code(200)
                .success(true)
                .message(message)
                .data(null)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * 创建失败响应
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 失败响应
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .code(500)
                .success(false)
                .message(message)
                .data(null)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * 创建失败响应（带状态码）
     *
     * @param code    状态码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 失败响应
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .success(false)
                .message(message)
                .data(null)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * 创建失败响应（带状态码和数据）
     *
     * @param code    状态码
     * @param message 错误消息
     * @param data    错误数据
     * @param <T>     数据类型
     * @return 失败响应
     */
    public static <T> ApiResponse<T> error(Integer code, String message, T data) {
        return ApiResponse.<T>builder()
                .code(code)
                .success(false)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * 创建创建成功响应
     *
     * @param data 创建的数据
     * @param <T>  数据类型
     * @return 创建成功响应
     */
    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .code(201)
                .success(true)
                .message("创建成功")
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * 创建更新成功响应
     *
     * @param data 更新的数据
     * @param <T>  数据类型
     * @return 更新成功响应
     */
    public static <T> ApiResponse<T> updated(T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .success(true)
                .message("更新成功")
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * 创建删除成功响应
     *
     * @param <T> 数据类型
     * @return 删除成功响应
     */
    public static <T> ApiResponse<T> deleted() {
        return ApiResponse.<T>builder()
                .code(200)
                .success(true)
                .message("删除成功")
                .data(null)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * 创建未找到响应
     *
     * @param message 消息
     * @param <T>     数据类型
     * @return 未找到响应
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return ApiResponse.<T>builder()
                .code(404)
                .success(false)
                .message(message)
                .data(null)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * 创建参数错误响应
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 参数错误响应
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return ApiResponse.<T>builder()
                .code(400)
                .success(false)
                .message(message)
                .data(null)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * 创建未授权响应
     *
     * @param message 消息
     * @param <T>     数据类型
     * @return 未授权响应
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return ApiResponse.<T>builder()
                .code(401)
                .success(false)
                .message(message)
                .data(null)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * 创建禁止访问响应
     *
     * @param message 消息
     * @param <T>     数据类型
     * @return 禁止访问响应
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        return ApiResponse.<T>builder()
                .code(403)
                .success(false)
                .message(message)
                .data(null)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * 设置追踪ID
     *
     * @param traceId 追踪ID
     * @return 当前实例
     */
    public ApiResponse<T> withTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }
}
