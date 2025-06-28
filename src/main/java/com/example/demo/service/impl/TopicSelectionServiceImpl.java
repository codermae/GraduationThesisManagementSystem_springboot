package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.TopicSelection;
import com.example.demo.mapper.TopicSelectionMapper;
import com.example.demo.service.TopicSelectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicSelectionServiceImpl implements TopicSelectionService {

    private final TopicSelectionMapper topicSelectionMapper;

    @Override
    public void createSelection(TopicSelection selection) {
        int result = topicSelectionMapper.insert(selection);
        if (result <= 0) {
            throw new RuntimeException("创建选题记录失败");
        }
    }

    @Override
    public void deleteSelection(String studentId) {
        int result = topicSelectionMapper.deleteById(studentId);
        if (result <= 0) {
            throw new RuntimeException("删除选题记录失败");
        }
    }

    @Override
    public TopicSelection getSelectionByStudent(String studentId) {
        return topicSelectionMapper.selectById(studentId);
    }

    @Override
    public List<TopicSelection> getSelectionsByTopic(String topicId) {
        return topicSelectionMapper.selectSelectionsByTopic(topicId);
    }

    @Override
    public List<TopicSelection> getSelectionsByTeacher(String teacherId) {
        return topicSelectionMapper.selectSelectionsByTeacher(teacherId);
    }
}