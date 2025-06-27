package com.example.demo.utils;

import javax.servlet.http.HttpServletRequest;

public class UserContextUtil {

    /**
     * 从请求中获取当前用户ID
     */
    public static String getCurrentUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        return userId != null ? userId.toString() : null;
    }

    /**
     * 从请求中获取当前用户角色
     */
    public static String getCurrentUserRole(HttpServletRequest request) {
        Object userRole = request.getAttribute("userRole");
        return userRole != null ? userRole.toString() : null;
    }

    /**
     * 判断当前用户是否为学生
     */
    public static boolean isStudent(HttpServletRequest request) {
        String role = getCurrentUserRole(request);
        return "student".equals(role);
    }

    /**
     * 判断当前用户是否为教师
     */
    public static boolean isTeacher(HttpServletRequest request) {
        String role = getCurrentUserRole(request);
        return "teacher".equals(role);
    }
}
