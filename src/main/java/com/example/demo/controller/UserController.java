package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.entity.Student;
import com.example.demo.entity.Teacher;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtil;
import com.example.demo.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordUtil passwordUtil; // 使用统一的密码工具类

    /**
     * 获取教师信息
     */
    @GetMapping("/teacher/{teacherId}")
    public Result<Teacher> getTeacher(@PathVariable String teacherId, HttpServletRequest request) {
        try {
            // 验证权限
            if (!hasPermission(request, teacherId, "teacher")) {
                return Result.error(403, "无权限访问");
            }

            Teacher teacher = userService.getTeacherById(teacherId);
            if (teacher == null) {
                return Result.error("教师不存在");
            }

            // 不返回密码
            teacher.setPassword(null);
            return Result.success("获取教师信息成功", teacher);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取学生信息
     */
    @GetMapping("/student/{studentId}")
    public Result<Student> getStudent(@PathVariable String studentId, HttpServletRequest request) {
        try {
            // 验证权限
            if (!hasPermission(request, studentId, "student")) {
                return Result.error(403, "无权限访问");
            }

            Student student = userService.getStudentById(studentId);
            if (student == null) {
                return Result.error("学生不存在");
            }

            // 不返回密码
            student.setPassword(null);
            return Result.success("获取学生信息成功", student);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新教师信息
     */
    @PutMapping("/teacher")
    public Result<Void> updateTeacher(@RequestBody Teacher teacher, HttpServletRequest request) {
        try {
            // 验证权限
            if (!hasPermission(request, teacher.getTeacherId(), "teacher")) {
                return Result.error(403, "无权限操作");
            }

            // 不允许通过此接口修改密码
            teacher.setPassword(null);
            userService.updateTeacherInfo(teacher);
            return Result.success("更新教师信息成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新学生信息
     */
    @PutMapping("/student")
    public Result<Void> updateStudent(@RequestBody Student student, HttpServletRequest request) {
        try {
            // 验证权限
            if (!hasPermission(request, student.getStudentId(), "student")) {
                return Result.error(403, "无权限操作");
            }

            // 不允许通过此接口修改密码
            student.setPassword(null);
            userService.updateStudentInfo(student);
            return Result.success("更新学生信息成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/change-password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request, HttpServletRequest httpRequest) {
        try {
            // 检查新旧密码是否相同
            if (request.getOldPassword().equals(request.getNewPassword())) {
                return Result.error("新密码不能与旧密码相同");
            }

            // 从token中获取用户信息
            String token = getTokenFromRequest(httpRequest);
            if (token == null || !jwtUtil.validateToken(token)) {
                return Result.error(401, "请重新登录");
            }

            String currentUserId = jwtUtil.getUserIdFromToken(token);
            String currentRole = jwtUtil.getRoleFromToken(token);

            // 调用服务层修改密码（服务层会进行密码格式验证）
            boolean success = userService.changePassword(currentUserId, currentRole,
                    request.getOldPassword(), request.getNewPassword());

            if (success) {
                return Result.success("密码修改成功", null);
            } else {
                return Result.error("密码修改失败，请检查旧密码是否正确");
            }
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("密码修改失败：" + e.getMessage());
        }
    }

    /**
     * 检查密码强度
     */
    @PostMapping("/check-password-strength")
    public Result<Map<String, String>> checkPasswordStrength(@RequestBody Map<String, String> request) {
        try {
            String password = request.get("password");
            if (password == null || password.trim().isEmpty()) {
                return Result.error("密码不能为空");
            }

            // 使用统一的密码工具类进行验证和强度检查
            String validationError = passwordUtil.validatePassword(password);
            if (validationError != null) {
                return Result.error(validationError);
            }

            String strength = passwordUtil.checkPasswordStrength(password);
            Map<String, String> result = new HashMap<>();
            result.put("strength", strength);
            result.put("message", "密码强度：" + strength);

            return Result.success("密码强度检查完成", result);
        } catch (Exception e) {
            return Result.error("检查密码强度失败：" + e.getMessage());
        }
    }

    /**
     * 验证用户权限
     */
    private boolean hasPermission(HttpServletRequest request, String targetUserId, String requiredRole) {
        try {
            String token = getTokenFromRequest(request);
            if (token == null || !jwtUtil.validateToken(token)) {
                return false;
            }

            String currentUserId = jwtUtil.getUserIdFromToken(token);
            String currentRole = jwtUtil.getRoleFromToken(token);

            // 只能访问自己的信息，且角色要匹配
            return currentUserId.equals(targetUserId) && currentRole.equals(requiredRole);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从请求中提取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}