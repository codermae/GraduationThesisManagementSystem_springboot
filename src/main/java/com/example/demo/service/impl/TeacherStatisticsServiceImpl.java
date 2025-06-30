// TeacherStatisticsServiceImpl.java - 服务实现
package com.example.demo.service.impl;

import com.example.demo.dto.TeacherStatisticsDTO;
import com.example.demo.entity.*;
import com.example.demo.mapper.*;
import com.example.demo.service.TeacherStatisticsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeacherStatisticsServiceImpl implements TeacherStatisticsService {

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private TopicSelectionMapper topicSelectionMapper;

    @Autowired
    private ThesisMapper thesisMapper;

    @Autowired
    private DefenseMapper defenseMapper;

    @Autowired
    private FinalGradeMapper finalGradeMapper;

    @Autowired
    private FileUploadMapper fileUploadMapper;

    @Override
    public TeacherStatisticsDTO getTeacherStatistics(String teacherId) {
        TeacherStatisticsDTO dto = new TeacherStatisticsDTO();

        // 1. 获取教师基本信息
        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) {
            throw new RuntimeException("教师不存在");
        }
        dto.setTeacherId(teacherId);
        dto.setTeacherName(teacher.getName());
        dto.setDepartment(teacher.getDepartment());

        // 2. 获取该教师的所有题目选择记录
        QueryWrapper<TopicSelection> selectionWrapper = new QueryWrapper<>();
        selectionWrapper.eq("teacher_id", teacherId);
        List<TopicSelection> selections = topicSelectionMapper.selectList(selectionWrapper);

        // 获取学生ID列表
        List<String> studentIds = selections.stream()
                .map(TopicSelection::getStudentId)
                .collect(Collectors.toList());

        // 3. 统计学生数量
        dto.setTotalStudentCount(studentIds.size());

        // 4. 获取学生成绩信息
        List<TeacherStatisticsDTO.StudentGradeInfo> studentGrades = new ArrayList<>();
        if (!studentIds.isEmpty()) {
            studentGrades = getStudentGradeInfos(studentIds, selections);
        }
        dto.setStudentGrades(studentGrades);

        // 5. 统计题目数量
        QueryWrapper<Topic> topicWrapper = new QueryWrapper<>();
        topicWrapper.eq("teacher_id", teacherId);
        Long topicCount = topicMapper.selectCount(topicWrapper);
        dto.setTotalTopicCount(topicCount.intValue());

        // 6. 统计评分情况
        calculateGradingStatistics(dto, studentIds);

        // 7. 统计文件分类
        calculateFileStatistics(dto, studentIds);

        // 8. 统计成绩分布
        calculateGradeDistribution(dto, studentGrades);

        return dto;
    }

    /**
     * 获取学生成绩详细信息
     */
    private List<TeacherStatisticsDTO.StudentGradeInfo> getStudentGradeInfos(
            List<String> studentIds, List<TopicSelection> selections) {

        List<TeacherStatisticsDTO.StudentGradeInfo> result = new ArrayList<>();

        // 批量查询学生信息
        List<Student> students = studentMapper.selectBatchIds(studentIds);
        Map<String, Student> studentMap = students.stream()
                .collect(Collectors.toMap(Student::getStudentId, s -> s));

        // 批量查询论文成绩
        QueryWrapper<Thesis> thesisWrapper = new QueryWrapper<>();
        thesisWrapper.in("student_id", studentIds);
        List<Thesis> theses = thesisMapper.selectList(thesisWrapper);
        Map<String, Thesis> thesisMap = theses.stream()
                .collect(Collectors.toMap(Thesis::getStudentId, t -> t));

        // 批量查询答辩成绩
        QueryWrapper<Defense> defenseWrapper = new QueryWrapper<>();
        defenseWrapper.in("student_id", studentIds);
        List<Defense> defenses = defenseMapper.selectList(defenseWrapper);
        Map<String, Defense> defenseMap = defenses.stream()
                .collect(Collectors.toMap(Defense::getStudentId, d -> d));

        // 批量查询最终成绩
        QueryWrapper<FinalGrade> finalWrapper = new QueryWrapper<>();
        finalWrapper.in("student_id", studentIds);
        List<FinalGrade> finalGrades = finalGradeMapper.selectList(finalWrapper);
        Map<String, FinalGrade> finalMap = finalGrades.stream()
                .collect(Collectors.toMap(FinalGrade::getStudentId, f -> f));

        // 批量查询题目信息
        List<String> topicIds = selections.stream()
                .map(TopicSelection::getTopicId)
                .distinct()
                .collect(Collectors.toList());
        List<Topic> topics = topicMapper.selectBatchIds(topicIds);
        Map<String, Topic> topicMap = topics.stream()
                .collect(Collectors.toMap(Topic::getTopicId, t -> t));

        // 构建结果
        for (TopicSelection selection : selections) {
            String studentId = selection.getStudentId();
            TeacherStatisticsDTO.StudentGradeInfo info = new TeacherStatisticsDTO.StudentGradeInfo();

            // 学生基本信息
            Student student = studentMap.get(studentId);
            if (student != null) {
                info.setStudentId(studentId);
                info.setStudentName(student.getName());
                info.setClassName(student.getClassName());
                info.setMajor(student.getMajor());
            }

            // 题目信息
            Topic topic = topicMap.get(selection.getTopicId());
            if (topic != null) {
                info.setTopicTitle(topic.getTitle());
            }

            // 论文成绩
            Thesis thesis = thesisMap.get(studentId);
            if (thesis != null) {
                info.setThesisScore(thesis.getThesisScore());
                info.setThesisRemarks(thesis.getRemarks());
                info.setHasThesisScore(true);
            } else {
                info.setHasThesisScore(false);
            }

            // 答辩成绩
            Defense defense = defenseMap.get(studentId);
            if (defense != null) {
                info.setDefenseScore(defense.getScore());
                info.setDefenseComments(defense.getComments());
                info.setHasDefenseScore(true);
            } else {
                info.setHasDefenseScore(false);
            }

            // 最终成绩
            FinalGrade finalGrade = finalMap.get(studentId);
            if (finalGrade != null) {
                info.setFinalScore(finalGrade.getFinalScore());
                info.setHasFinalScore(true);
            } else {
                info.setHasFinalScore(false);
            }

            result.add(info);
        }

        return result;
    }

    /**
     * 计算评分统计
     */
    private void calculateGradingStatistics(TeacherStatisticsDTO dto, List<String> studentIds) {
        if (studentIds.isEmpty()) {
            dto.setCompletedGradingCount(0);
            dto.setPendingGradingCount(0);
            return;
        }

        // 统计已有论文成绩的学生数
        QueryWrapper<Thesis> thesisWrapper = new QueryWrapper<>();
        thesisWrapper.in("student_id", studentIds);
        Long completedThesis = thesisMapper.selectCount(thesisWrapper);

        // 统计已有答辩成绩的学生数
        QueryWrapper<Defense> defenseWrapper = new QueryWrapper<>();
        defenseWrapper.in("student_id", studentIds);
        Long completedDefense = defenseMapper.selectCount(defenseWrapper);

        // 已完成评分 = 论文成绩 + 答辩成绩
        int completed = completedThesis.intValue() + completedDefense.intValue();

        // 未完成评分 = 总应评分数 - 已完成评分数
        // 每个学生需要论文和答辩两项评分
        int totalRequired = studentIds.size() * 2;
        int pending = totalRequired - completed;

        dto.setCompletedGradingCount(completed);
        dto.setPendingGradingCount(Math.max(0, pending));
    }

    /**
     * 计算文件统计
     */
    private void calculateFileStatistics(TeacherStatisticsDTO dto, List<String> studentIds) {
        Map<String, Integer> fileStats = new HashMap<>();

        if (studentIds.isEmpty()) {
            dto.setFileTypeStatistics(fileStats);
            return;
        }

        // 查询所有相关文件
        QueryWrapper<FileUpload> fileWrapper = new QueryWrapper<>();
        fileWrapper.in("student_id", studentIds);
        fileWrapper.eq("is_deleted", false); // 只统计未删除的文件
        List<FileUpload> files = fileUploadMapper.selectList(fileWrapper);

        // 按文件类别分组统计
        Map<String, List<FileUpload>> groupedFiles = files.stream()
                .collect(Collectors.groupingBy(file ->
                        file.getFileCategory() != null ? file.getFileCategory() : "OTHER"));

        // 转换为统计结果
        for (Map.Entry<String, List<FileUpload>> entry : groupedFiles.entrySet()) {
            String category = entry.getKey();
            int count = entry.getValue().size();

            // 转换为中文描述
            String categoryName = getCategoryDisplayName(category);
            fileStats.put(categoryName, count);
        }

        dto.setFileTypeStatistics(fileStats);
    }

    /**
     * 获取文件类别的显示名称
     */
    private String getCategoryDisplayName(String category) {
        return category != null ? category.toUpperCase() : "OTHER";
    }

    /**
     * 计算成绩分布统计
     */
    private void calculateGradeDistribution(TeacherStatisticsDTO dto, List<TeacherStatisticsDTO.StudentGradeInfo> studentGrades) {
        int excellentCount = 0;  // 优秀(90-100)
        int goodCount = 0;       // 良好(80-89)
        int passCount = 0;       // 及格(60-79)
        int failCount = 0;       // 不及格(0-59)

        for (TeacherStatisticsDTO.StudentGradeInfo student : studentGrades) {
            // 使用最终成绩进行分级，如果没有最终成绩则跳过
            if (student.getFinalScore() != null) {
                double score = student.getFinalScore().doubleValue();
                if (score >= 90) {
                    excellentCount++;
                } else if (score >= 80) {
                    goodCount++;
                } else if (score >= 60) {
                    passCount++;
                } else {
                    failCount++;
                }
            }
        }

        dto.setExcellentCount(excellentCount);
        dto.setGoodCount(goodCount);
        dto.setPassCount(passCount);
        dto.setFailCount(failCount);
    }
}