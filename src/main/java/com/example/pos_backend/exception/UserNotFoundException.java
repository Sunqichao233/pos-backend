package com.example.pos_backend.exception;

/**
 * 用户未找到异常
 * 当根据ID或其他条件查找用户时，如果用户不存在则抛出此异常
 */
public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 默认构造函数
     */
    public UserNotFoundException() {
        super("用户不存在");
    }

    /**
     * 带消息的构造函数
     *
     * @param message 异常消息
     */
    public UserNotFoundException(String message) {
        super(message);
    }

    /**
     * 带消息和原因的构造函数
     *
     * @param message 异常消息
     * @param cause   异常原因
     */
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 根据用户ID创建异常
     *
     * @param userId 用户ID
     * @return UserNotFoundException 实例
     */
    public static UserNotFoundException withId(Long userId) {
        return new UserNotFoundException("用户不存在，ID: " + userId);
    }

    /**
     * 根据用户名创建异常
     *
     * @param username 用户名
     * @return UserNotFoundException 实例
     */
    public static UserNotFoundException withUsername(String username) {
        return new UserNotFoundException("用户不存在，用户名: " + username);
    }

    /**
     * 根据邮箱创建异常
     *
     * @param email 邮箱
     * @return UserNotFoundException 实例
     */
    public static UserNotFoundException withEmail(String email) {
        return new UserNotFoundException("用户不存在，邮箱: " + email);
    }
}
