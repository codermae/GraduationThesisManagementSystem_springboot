package com.example.demo.service.impl;

import com.example.demo.entity.Student;
import com.example.demo.entity.Teacher;
import com.example.demo.mapper.StudentMapper;
import com.example.demo.mapper.TeacherMapper;
import com.example.demo.service.UserService;
import com.example.demo.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final TeacherMapper teacherMapper;
    private final StudentMapper studentMapper;
    private final PasswordEncoder passwordEncoder; // 直接使用已有的PasswordEncoder
    private final PasswordUtil passwordUtil;

    @Override
    public Teacher getTeacherById(String teacherId) {
        return teacherMapper.selectById(teacherId);
    }

    @Override
    public Student getStudentById(String studentId) {
        return studentMapper.selectById(studentId);
    }

    @Override
    public void updateTeacherInfo(Teacher teacher) {
        teacherMapper.updateById(teacher);
    }

    @Override
    public void updateStudentInfo(Student student) {
        studentMapper.updateById(student);
    }

    @Override
    public boolean changePassword(String userId, String userType, String oldPassword, String newPassword) {
        try {
            // 验证新密码格式
            String validationError = passwordUtil.validatePassword(newPassword);
            if (validationError != null) {
                throw new IllegalArgumentException(validationError);
            }

            // 根据用户类型获取当前用户信息
            if ("teacher".equals(userType)) {
                Teacher teacher = teacherMapper.selectById(userId);
                if (teacher == null) {
                    return false;
                }
                // 验证旧密码（使用PasswordEncoder，与登录验证保持一致）
                if (!passwordEncoder.matches(oldPassword, teacher.getPassword())) {
                    return false;
                }
                // 更新新密码（使用PasswordEncoder加密，与注册时保持一致）
                teacher.setPassword(passwordEncoder.encode(newPassword));
                teacherMapper.updateById(teacher);
                return true;
            } else if ("student".equals(userType)) {
                Student student = studentMapper.selectById(userId);
                if (student == null) {
                    return false;
                }
                // 验证旧密码（使用PasswordEncoder，与登录验证保持一致）
                if (!passwordEncoder.matches(oldPassword, student.getPassword())) {
                    return false;
                }
                // 更新新密码（使用PasswordEncoder加密，与注册时保持一致）
                student.setPassword(passwordEncoder.encode(newPassword));
                studentMapper.updateById(student);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}