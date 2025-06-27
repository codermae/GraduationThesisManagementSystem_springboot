package com.example.demo.constant;

public class SystemConstants {
    // 角色常量
    public static final String ROLE_STUDENT = "student";
    public static final String ROLE_TEACHER = "teacher";

    // JWT相关常量
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";

    // 响应状态码
    public static final int SUCCESS_CODE = 200;
    public static final int ERROR_CODE = 500;
    public static final int UNAUTHORIZED_CODE = 401;
    public static final int FORBIDDEN_CODE = 403;

    // 响应消息
    public static final String SUCCESS_MESSAGE = "操作成功";
    public static final String ERROR_MESSAGE = "操作失败";
    public static final String UNAUTHORIZED_MESSAGE = "未授权访问";
    public static final String FORBIDDEN_MESSAGE = "禁止访问";

    private SystemConstants() {}

}
