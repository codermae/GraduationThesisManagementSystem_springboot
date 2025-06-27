package com.example.demo.service.impl;

import com.example.demo.entity.Student;
import com.example.demo.entity.Teacher;
import com.example.demo.mapper.StudentMapper;
import com.example.demo.mapper.TeacherMapper;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final TeacherMapper teacherMapper;
    private final StudentMapper studentMapper;

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
}