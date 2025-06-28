package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.dto.*;
import com.example.demo.entity.TopicSelection;

import java.util.List;

public interface TopicService {

    /**
     * 创建题目（教师）
     */
    void createTopic(TopicCreateRequest request);

    /**
     * 更新题目（教师）
     */
    void updateTopic(TopicUpdateRequest request);

    /**
     * 删除题目（教师）
     */
    void deleteTopic(String topicId);

    /**
     * 分页查询题目列表
     */
    IPage<TopicResponse> getTopicPage(TopicQueryRequest request);

    /**
     * 查询题目详情
     */
    TopicResponse getTopicDetail(String topicId);

    /**
     * 查询教师发布的题目列表
     */
    List<TopicResponse> getTopicsByTeacher(String teacherId);

    /**
     * 学生选题
     */
    void selectTopic(TopicSelectionRequest request);

    /**
     * 学生取消选题
     */
    void cancelTopicSelection();

    /**
     * 查询学生已选择的题目
     */
    TopicResponse getStudentSelectedTopic();

    /**
     * 查询教师指导的学生选题情况
     */
    List<TopicSelection> getSelectionsByTeacher(String teacherId);

    /**
     * 查询某个题目的选择情况
     */
    List<TopicSelection> getSelectionsByTopic(String topicId);
}