package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.Student;
import com.example.demo.entity.Teacher;
import com.example.demo.entity.UserRole;
import com.example.demo.mapper.StudentMapper;
import com.example.demo.mapper.TeacherMapper;
import com.example.demo.mapper.UserRoleMapper;
import com.example.demo.service.AuthService;
import com.example.demo.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final TeacherMapper teacherMapper;
    private final StudentMapper studentMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        // 验证用户角色
        UserRole userRole = userRoleMapper.selectById(request.getUserId());
        if (userRole == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!userRole.getRole().equals(request.getRole())) {
            throw new RuntimeException("角色不匹配");
        }

        LoginResponse response = new LoginResponse();
        response.setUserId(request.getUserId());
        response.setRole(request.getRole());

        // 根据角色验证密码
        if ("teacher".equals(request.getRole())) {
            Teacher teacher = teacherMapper.selectById(request.getUserId());
            if (teacher == null) {
                throw new RuntimeException("教师不存在");
            }

            if (!passwordEncoder.matches(request.getPassword(), teacher.getPassword())) {
                throw new RuntimeException("密码错误");
            }

            response.setName(teacher.getName());
            response.setDepartment(teacher.getDepartment());

        } else if ("student".equals(request.getRole())) {
            Student student = studentMapper.selectById(request.getUserId());
            if (student == null) {
                throw new RuntimeException("学生不存在");
            }

            if (!passwordEncoder.matches(request.getPassword(), student.getPassword())) {
                throw new RuntimeException("密码错误");
            }

            response.setName(student.getName());
            response.setClassName(student.getClassName());
            response.setMajor(student.getMajor());
        }

        // 生成JWT token
        String token = jwtUtil.generateToken(request.getUserId(), request.getRole());
        response.setToken(token);

        return response;
    }

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        // 检查用户是否已存在
        UserRole existingRole = userRoleMapper.selectById(request.getUserId());
        if (existingRole != null) {
            throw new RuntimeException("用户ID已存在");
        }

        // 加密密码
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 创建用户角色记录
        UserRole userRole = new UserRole();
        userRole.setUserId(request.getUserId());
        userRole.setRole(request.getRole());
        userRoleMapper.insert(userRole);

        // 根据角色创建对应的用户记录
        if ("teacher".equals(request.getRole())) {
            Teacher teacher = new Teacher();
            teacher.setTeacherId(request.getUserId());
            teacher.setName(request.getName());
            teacher.setDepartment(request.getDepartment());
            teacher.setPassword(encodedPassword);
            teacherMapper.insert(teacher);

        } else if ("student".equals(request.getRole())) {
            Student student = new Student();
            student.setStudentId(request.getUserId());
            student.setName(request.getName());
            student.setClassName(request.getClassName());
            student.setMajor(request.getMajor());
            student.setPassword(encodedPassword);
            studentMapper.insert(student);
        } else {
            throw new RuntimeException("无效的角色类型");
        }
    }

    @Override
    public void logout(String token) {
        // 将token加入黑名单（可以使用Redis存储）
        // 这里先简单实现，实际项目中建议使用Redis
        jwtUtil.invalidateToken(token);
    }
}