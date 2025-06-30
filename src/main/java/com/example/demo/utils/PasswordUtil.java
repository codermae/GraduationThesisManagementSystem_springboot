package com.example.demo.utils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 加密密码
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 验证密码
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 检查密码强度
     * @param password 密码
     * @return 密码强度描述
     */
    public String checkPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return "密码长度至少6位";
        }

        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

        int score = 0;
        if (hasLower) score++;
        if (hasUpper) score++;
        if (hasNumber) score++;
        if (hasSpecial) score++;

        switch (score) {
            case 0:
            case 1:
                return "弱";
            case 2:
                return "中等";
            case 3:
                return "强";
            case 4:
                return "很强";
            default:
                return "未知";
        }
    }

    /**
     * 验证密码格式
     * @param password 密码
     * @return 验证结果
     */
    public String validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return "密码不能为空";
        }

        if (password.length() < 6) {
            return "密码长度不能少于6位";
        }

        if (password.length() > 20) {
            return "密码长度不能超过20位";
        }

        // 检查是否包含至少一个字母和一个数字
        if (!password.matches(".*[a-zA-Z].*") || !password.matches(".*\\d.*")) {
            return "密码必须包含至少一个字母和一个数字";
        }

        return null; // 验证通过
    }
}