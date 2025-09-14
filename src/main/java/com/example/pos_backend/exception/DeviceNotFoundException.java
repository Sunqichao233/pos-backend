package com.example.pos_backend.exception;

/**
 * 设备未找到异常
 * 当根据ID或其他条件查找设备时，如果设备不存在则抛出此异常
 */
public class DeviceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 默认构造函数
     */
    public DeviceNotFoundException() {
        super("设备不存在");
    }

    /**
     * 带消息的构造函数
     *
     * @param message 异常消息
     */
    public DeviceNotFoundException(String message) {
        super(message);
    }

    /**
     * 带消息和原因的构造函数
     *
     * @param message 异常消息
     * @param cause   异常原因
     */
    public DeviceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 根据设备ID创建异常
     *
     * @param deviceId 设备ID
     * @return DeviceNotFoundException 实例
     */
    public static DeviceNotFoundException withId(Long deviceId) {
        return new DeviceNotFoundException("设备不存在，ID: " + deviceId);
    }

    /**
     * 根据设备名称创建异常
     *
     * @param deviceName 设备名称
     * @return DeviceNotFoundException 实例
     */
    public static DeviceNotFoundException withDeviceName(String deviceName) {
        return new DeviceNotFoundException("设备不存在，设备名称: " + deviceName);
    }

    /**
     * 根据MAC地址创建异常
     *
     * @param macAddress MAC地址
     * @return DeviceNotFoundException 实例
     */
    public static DeviceNotFoundException withMacAddress(String macAddress) {
        return new DeviceNotFoundException("设备不存在，MAC地址: " + macAddress);
    }

    /**
     * 根据IP地址创建异常
     *
     * @param ipAddress IP地址
     * @return DeviceNotFoundException 实例
     */
    public static DeviceNotFoundException withIpAddress(String ipAddress) {
        return new DeviceNotFoundException("设备不存在，IP地址: " + ipAddress);
    }
}
