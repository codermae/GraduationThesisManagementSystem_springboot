package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.*;
import com.example.demo.entity.Topic;
import com.example.demo.entity.TopicSelection;
import com.example.demo.mapper.TopicMapper;
import com.example.demo.service.TopicSelectionService;
import com.example.demo.service.TopicService;
import com.example.demo.utils.UserContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicMapper topicMapper;
    private final TopicSelectionService topicSelectionService;

    @Override
    public void createTopic(TopicCreateRequest request) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String currentUserId = UserContextUtil.getCurrentUserId(httpRequest);
        String currentRole = UserContextUtil.getCurrentUserRole(httpRequest);

        if (!"teacher".equals(currentRole)) {
            throw new RuntimeException("只有教师可以创建题目");
        }

        Topic topic = new Topic();
        BeanUtils.copyProperties(request, topic);
        topic.setTopicId(generateTopicId());
        topic.setTeacherId(currentUserId);

        int result = topicMapper.insert(topic);
        if (result <= 0) {
            throw new RuntimeException("创建题目失败");
        }
    }

    @Override
    public void updateTopic(TopicUpdateRequest request) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String currentUserId = UserContextUtil.getCurrentUserId(httpRequest);
        String currentRole = UserContextUtil.getCurrentUserRole(httpRequest);

        if (!"teacher".equals(currentRole)) {
            throw new RuntimeException("只有教师可以修改题目");
        }

        // 检查题目是否存在且属于当前教师
        Topic existingTopic = topicMapper.selectById(request.getTopicId());
        if (existingTopic == null) {
            throw new RuntimeException("题目不存在");
        }

        if (!currentUserId.equals(existingTopic.getTeacherId())) {
            System.out.println(currentUserId);
            System.out.println(existingTopic.getTeacherId());
            System.out.println(!currentUserId.equals(existingTopic.getTeacherId()));
            throw new RuntimeException("只能修改自己发布的题目");
        }

        // 检查题目是否已被选择
        int selectionCount = topicMapper.countTopicSelections(request.getTopicId());
        if (selectionCount > 0) {
            throw new RuntimeException("题目已被学生选择，无法修改");
        }

        Topic topic = new Topic();
        BeanUtils.copyProperties(request, topic);

        int result = topicMapper.updateById(topic);
        if (result <= 0) {
            throw new RuntimeException("更新题目失败");
        }
    }

    @Override
    public void deleteTopic(String topicId) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String currentUserId = UserContextUtil.getCurrentUserId(httpRequest);
        String currentRole = UserContextUtil.getCurrentUserRole(httpRequest);

        if (!"teacher".equals(currentRole)) {
            throw new RuntimeException("只有教师可以删除题目");
        }

        // 检查题目是否存在且属于当前教师
        Topic existingTopic = topicMapper.selectById(topicId);
        if (existingTopic == null) {
            throw new RuntimeException("题目不存在");
        }

        if (!currentUserId.equals(existingTopic.getTeacherId())) {
            throw new RuntimeException("只能删除自己发布的题目");
        }

        // 检查题目是否已被选择
        int selectionCount = topicMapper.countTopicSelections(topicId);
        if (selectionCount > 0) {
            throw new RuntimeException("题目已被学生选择，无法删除");
        }

        int result = topicMapper.deleteById(topicId);
        if (result <= 0) {
            throw new RuntimeException("删除题目失败");
        }
    }

    @Override
    public IPage<TopicResponse> getTopicPage(TopicQueryRequest request) {
        Page<TopicResponse> page = new Page<>(request.getPage(), request.getSize());
        return topicMapper.selectTopicPage(page, request);
    }

    @Override
    public TopicResponse getTopicDetail(String topicId) {
        return topicMapper.selectTopicDetail(topicId);
    }

    @Override
    public List<TopicResponse> getTopicsByTeacher(String teacherId) {
        return topicMapper.selectTopicsByTeacher(teacherId);
    }

    @Override
    @Transactional
    public void selectTopic(TopicSelectionRequest request) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String currentUserId = UserContextUtil.getCurrentUserId(httpRequest);
        String currentRole = UserContextUtil.getCurrentUserRole(httpRequest);

        if (!"student".equals(currentRole)) {
            throw new RuntimeException("只有学生可以选择题目");
        }

        // 检查题目是否存在
        Topic topic = topicMapper.selectById(request.getTopicId());
        if (topic == null) {
            throw new RuntimeException("题目不存在");
        }

        // 检查学生是否已经选择了题目
        TopicSelection existingSelection = topicSelectionService.getSelectionByStudent(currentUserId);
        if (existingSelection != null) {
            throw new RuntimeException("您已经选择了题目，请先取消当前选题");
        }

        // 检查题目是否已被其他学生选择
        int selectionCount = topicMapper.countTopicSelections(request.getTopicId());
        if (selectionCount > 0) {
            throw new RuntimeException("该题目已被其他学生选择");
        }

        // 创建选题记录
        TopicSelection selection = new TopicSelection();
        selection.setStudentId(currentUserId);
        selection.setTopicId(request.getTopicId());
        selection.setTeacherId(topic.getTeacherId());
        selection.setSelectionDate(LocalDateTime.now());

        topicSelectionService.createSelection(selection);
    }

    @Override
    public void cancelTopicSelection() {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String currentUserId = UserContextUtil.getCurrentUserId(httpRequest);
        String currentRole = UserContextUtil.getCurrentUserRole(httpRequest);

        if (!"student".equals(currentRole)) {
            throw new RuntimeException("只有学生可以取消选题");
        }

        TopicSelection existingSelection = topicSelectionService.getSelectionByStudent(currentUserId);
        if (existingSelection == null) {
            throw new RuntimeException("您还没有选择题目");
        }

        topicSelectionService.deleteSelection(currentUserId);
    }

    @Override
    public TopicResponse getStudentSelectedTopic() {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String currentUserId = UserContextUtil.getCurrentUserId(httpRequest);
        return topicMapper.selectStudentSelectedTopic(currentUserId);
    }

    @Override
    public List<TopicSelection> getSelectionsByTeacher(String teacherId) {
        return topicSelectionService.getSelectionsByTeacher(teacherId);
    }

    @Override
    public List<TopicSelection> getSelectionsByTopic(String topicId) {
        return topicSelectionService.getSelectionsByTopic(topicId);
    }

    /**
     * 生成题目ID
     */
    private String generateTopicId() {
        return "T" + System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    @Override
    public List<StudentInfoResponse> getStudentsInfoByTeacher(String teacherId) {
        return topicMapper.selectStudentsInfoByTeacher(teacherId);
    }

    @Override
    public StudentInfoResponse getStudentDetailInfo(String studentId) {
        return topicMapper.selectStudentDetailInfo(studentId);
    }

    @Override
    public boolean isTeacherStudent(String teacherId, String studentId) {
        int count = topicMapper.countTeacherStudent(teacherId, studentId);
        return count > 0;
    }
}
