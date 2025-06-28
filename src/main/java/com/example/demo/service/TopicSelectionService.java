package com.example.demo.service;

import com.example.demo.entity.TopicSelection;

import java.util.List;

public interface TopicSelectionService {

    /**
     * 创建选题记录
     */
    void createSelection(TopicSelection selection);

    /**
     * 删除选题记录
     */
    void deleteSelection(String studentId);

    /**
     * 查询学生选题记录
     */
    TopicSelection getSelectionByStudent(String studentId);

    /**
     * 查询题目选择记录
     */
    List<TopicSelection> getSelectionsByTopic(String topicId);

    /**
     * 查询教师指导的选题记录
     */
    List<TopicSelection> getSelectionsByTeacher(String teacherId);
}