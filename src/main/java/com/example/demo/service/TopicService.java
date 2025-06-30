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
     * 获取老师名下的学生信息列表
     * @param teacherId 教师ID
     * @return 学生信息列表
     */
    List<StudentInfoResponse> getStudentsInfoByTeacher(String teacherId);

    /**
     * 获取指定学生的详细信息
     * @param studentId 学生ID
     * @return 学生详细信息
     */
    StudentInfoResponse getStudentDetailInfo(String studentId);

    /**
     * 验证学生是否是指定老师的学生
     * @param teacherId 教师ID
     * @param studentId 学生ID
     * @return 是否是该老师的学生
     */
    boolean isTeacherStudent(String teacherId, String studentId);

    /**
     * 查询某个题目的选择情况
     */
    List<TopicSelection> getSelectionsByTopic(String topicId);
}