package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Student;
import com.example.demo.entity.Teacher;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

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