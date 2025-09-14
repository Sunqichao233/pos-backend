package com.example.pos_backend.exception;

/**
 * 设备码未找到异常
 * 当根据ID或设备码查找设备码记录时，如果设备码不存在则抛出此异常
 */
public class DeviceCodeNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 默认构造函数
     */
    public DeviceCodeNotFoundException() {
        super("设备码不存在");
    }

    /**
     * 带消息的构造函数
     *
     * @param message 异常消息
     */
    public DeviceCodeNotFoundException(String message) {
        super(message);
    }

    /**
     * 带消息和原因的构造函数
     *
     * @param message 异常消息
     * @param cause   异常原因
     */
    public DeviceCodeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 根据设备码ID创建异常
     *
     * @param deviceCodeId 设备码ID
     * @return DeviceCodeNotFoundException 实例
     */
    public static DeviceCodeNotFoundException withId(Long deviceCodeId) {
        return new DeviceCodeNotFoundException("设备码不存在，ID: " + deviceCodeId);
    }

    /**
     * 根据设备码创建异常
     *
     * @param deviceCode 设备码
     * @return DeviceCodeNotFoundException 实例
     */
    public static DeviceCodeNotFoundException withDeviceCode(String deviceCode) {
        return new DeviceCodeNotFoundException("设备码不存在，设备码: " + deviceCode);
    }

    /**
     * 根据设备ID创建异常
     *
     * @param deviceId 设备ID
     * @return DeviceCodeNotFoundException 实例
     */
    public static DeviceCodeNotFoundException withDeviceId(Long deviceId) {
        return new DeviceCodeNotFoundException("设备未找到对应的设备码，设备ID: " + deviceId);
    }

    /**
     * 根据状态创建异常
     *
     * @param status 状态
     * @return DeviceCodeNotFoundException 实例
     */
    public static DeviceCodeNotFoundException withStatus(String status) {
        return new DeviceCodeNotFoundException("未找到指定状态的设备码，状态: " + status);
    }
}
