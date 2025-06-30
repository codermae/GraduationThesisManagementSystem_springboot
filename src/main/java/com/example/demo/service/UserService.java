package com.example.demo.service;

import com.example.demo.entity.Student;
import com.example.demo.entity.Teacher;

public interface UserService {
    Teacher getTeacherById(String teacherId);
    Student getStudentById(String studentId);
    void updateTeacherInfo(Teacher teacher);
    void updateStudentInfo(Student student);
    boolean changePassword(String userId, String userType, String oldPassword, String newPassword);

}
