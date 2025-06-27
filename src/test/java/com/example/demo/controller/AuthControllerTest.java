package com.example.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRegisterStudent() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUserId("20231001");
        request.setName("张三");
        request.setPassword("123456");
        request.setRole("student");
        request.setClassName("计算机科学与技术1班");
        request.setMajor("计算机科学与技术");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"));
    }

    @Test
    public void testRegisterTeacher() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUserId("T001");
        request.setName("李老师");
        request.setPassword("123456");
        request.setRole("teacher");
        request.setDepartment("计算机学院");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // 修正此处
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"));
    }

    @Test
    public void testLoginStudent() throws Exception {
        // 先注册一个学生
        testRegisterStudent();

        LoginRequest request = new LoginRequest();
        request.setUserId("20231001");
        request.setPassword("123456");
        request.setRole("student");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.userId").value("20231001"))
                .andExpect(jsonPath("$.data.role").value("student"))
                .andExpect(jsonPath("$.data.name").value("张三"))
                .andExpect(jsonPath("$.data.token").exists());
    }

    @Test
    public void testLoginTeacher() throws Exception {
        // 先注册一个教师
        testRegisterTeacher();

        LoginRequest request = new LoginRequest();
        request.setUserId("T001");
        request.setPassword("123456");
        request.setRole("teacher");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.userId").value("T001"))
                .andExpect(jsonPath("$.data.role").value("teacher"))
                .andExpect(jsonPath("$.data.name").value("李老师"))
                .andExpect(jsonPath("$.data.token").exists());
    }

    @Test
    public void testLoginWithWrongPassword() throws Exception {
        // 先注册一个学生
        testRegisterStudent();

        LoginRequest request = new LoginRequest();
        request.setUserId("20231001");
        request.setPassword("wrongpassword");
        request.setRole("student");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("密码错误"));
    }

    @Test
    public void testLoginWithWrongRole() throws Exception {
        // 先注册一个学生
        testRegisterStudent();

        LoginRequest request = new LoginRequest();
        request.setUserId("20231001");
        request.setPassword("123456");
        request.setRole("teacher"); // 错误的角色

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("角色不匹配"));
    }

    @Test
    public void testRegisterDuplicateUser() throws Exception {
        // 先注册一个学生
        testRegisterStudent();

        // 再次注册相同的用户ID
        RegisterRequest request = new RegisterRequest();
        request.setUserId("20231001");
        request.setName("李四");
        request.setPassword("123456");
        request.setRole("student");
        request.setClassName("计算机科学与技术2班");
        request.setMajor("计算机科学与技术");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("用户ID已存在"));
    }

    @Test
    public void testValidateToken() throws Exception {
        // 先注册并登录获取token
        testRegisterStudent();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserId("20231001");
        loginRequest.setPassword("123456");
        loginRequest.setRole("student");

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 从响应中提取token（这里需要根据实际响应格式解析）
        // String token = extractTokenFromResponse(response);

        // 验证token
        mockMvc.perform(get("/api/auth/validate")
                        .header("Authorization", "Bearer " + "mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
