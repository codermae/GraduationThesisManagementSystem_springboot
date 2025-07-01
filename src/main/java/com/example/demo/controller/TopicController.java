package com.example.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.common.Result;
import com.example.demo.dto.*;
import com.example.demo.entity.TopicSelection;
import com.example.demo.service.TopicService;
import com.example.demo.utils.UserContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    /**
     * 创建题目（教师）
     */
    @PostMapping
    public Result<Void> createTopic(@Valid @RequestBody TopicCreateRequest request) {
        topicService.createTopic(request);
        return Result.success();
    }

    /**
     * 更新题目（教师）
     */
    @PutMapping
    public Result<Void> updateTopic(@Valid @RequestBody TopicUpdateRequest request) {
        topicService.updateTopic(request);
        return Result.success();
    }

    /**
     * 删除题目（教师）
     */
    @DeleteMapping("/{topicId}")
    public Result<Void> deleteTopic(@PathVariable String topicId) {
        topicService.deleteTopic(topicId);
        return Result.success();
    }

    /**
     * 分页查询题目列表
     */
    @GetMapping
    public Result<IPage<TopicResponse>> getTopicPage(TopicQueryRequest request) {
        System.out.println("============================================");
        System.out.println("TopicQueryRequest: " + request); // 打印请求参数
        IPage<TopicResponse> page = topicService.getTopicPage(request);
        return Result.success(page);
    }

    /**
     * 查询题目详情
     */
    @GetMapping("/{topicId}")
    public Result<TopicResponse> getTopicDetail(@PathVariable String topicId) {
        TopicResponse topic = topicService.getTopicDetail(topicId);
        return Result.success(topic);
    }

    /**
     * 查询我发布的题目列表（教师）
     */
    @GetMapping("/my-topics")
    public Result<List<TopicResponse>> getMyTopics(HttpServletRequest request) {
        String currentUserId = UserContextUtil.getCurrentUserId(request);
        String currentRole = UserContextUtil.getCurrentUserRole(request);

        if (!"teacher".equals(currentRole)) {
            return Result.error("只有教师可以查看发布的题目");
        }

        List<TopicResponse> topics = topicService.getTopicsByTeacher(currentUserId);
        return Result.success(topics);
    }

    /**
     * 查询指定教师的题目列表
     */
    @GetMapping("/teacher/{teacherId}")
    public Result<List<TopicResponse>> getTopicsByTeacher(@PathVariable String teacherId) {
        List<TopicResponse> topics = topicService.getTopicsByTeacher(teacherId);
        return Result.success(topics);
    }

    /**
     * 学生选题
     */
    @PostMapping("/select")
    public Result<Void> selectTopic(@Valid @RequestBody TopicSelectionRequest request) {
        topicService.selectTopic(request);
        return Result.success();
    }

    /**
     * 学生取消选题
     */
    @DeleteMapping("/select")
    public Result<Void> cancelTopicSelection() {
        topicService.cancelTopicSelection();
        return Result.success();
    }

    /**
     * 查询学生已选择的题目
     */
    @GetMapping("/my-selection")
    public Result<TopicResponse> getMySelectedTopic(HttpServletRequest request) {
        String currentRole = UserContextUtil.getCurrentUserRole(request);

        if (!"student".equals(currentRole)) {
            return Result.error("只有学生可以查看已选题目");
        }

        TopicResponse topic = topicService.getStudentSelectedTopic();
        return Result.success(topic);
    }

    /**
     * 查询我指导的学生选题情况（教师）
     */
    @GetMapping("/my-students")
    public Result<List<TopicSelection>> getMyStudentSelections(HttpServletRequest request) {
        String currentUserId = UserContextUtil.getCurrentUserId(request);
        String currentRole = UserContextUtil.getCurrentUserRole(request);

        if (!"teacher".equals(currentRole)) {
            return Result.error("只有教师可以查看指导的学生");
        }

        List<TopicSelection> selections = topicService.getSelectionsByTeacher(currentUserId);
        return Result.success(selections);
    }
    /**
     * 获取老师名下的学生信息列表（包含选题、论文、答辩等详细信息）
     */
    @GetMapping("/my-students-info")
    public Result<List<StudentInfoResponse>> getMyStudentsInfo(HttpServletRequest request) {
        String currentUserId = UserContextUtil.getCurrentUserId(request);
        String currentRole = UserContextUtil.getCurrentUserRole(request);

        if (!"teacher".equals(currentRole)) {
            return Result.error("只有教师可以查看指导的学生信息");
        }

        List<StudentInfoResponse> studentsInfo = topicService.getStudentsInfoByTeacher(currentUserId);
        return Result.success(studentsInfo);
    }
    /**
     * 获取指定学生的详细信息（老师专用）
     */
    @GetMapping("/student-detail/{studentId}")
    public Result<StudentInfoResponse> getStudentDetail(@PathVariable String studentId, HttpServletRequest request) {
        String currentUserId = UserContextUtil.getCurrentUserId(request);
        String currentRole = UserContextUtil.getCurrentUserRole(request);

        if (!"teacher".equals(currentRole)) {
            return Result.error("只有教师可以查看学生详细信息");
        }

        // 验证该学生是否是当前老师指导的学生
        boolean isMyStudent = topicService.isTeacherStudent(currentUserId, studentId);
        if (!isMyStudent) {
            return Result.error("您无权查看该学生信息");
        }

        StudentInfoResponse studentInfo = topicService.getStudentDetailInfo(studentId);
        return Result.success(studentInfo);
    }

    /**
     * 查询题目的选择情况（教师）
     */
    @GetMapping("/{topicId}/selections")
    public Result<List<TopicSelection>> getTopicSelections(@PathVariable String topicId, HttpServletRequest request) {
        String currentRole = UserContextUtil.getCurrentUserRole(request);

        if (!"teacher".equals(currentRole)) {
            return Result.error("只有教师可以查看题目选择情况");
        }

        List<TopicSelection> selections = topicService.getSelectionsByTopic(topicId);
        return Result.success(selections);
    }
}
