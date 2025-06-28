// GradeServiceImpl.java
package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.exception.GradeBusinessException;
import com.example.demo.mapper.*;
import com.example.demo.service.GradeService;
import com.example.demo.utils.UserContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.ByteArrayOutputStream;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    private final ThesisMapper thesisMapper;
    private final DefenseMapper defenseMapper;
    private final FinalGradeMapper finalGradeMapper;
    private final TopicSelectionMapper topicSelectionMapper;
    private final StudentMapper studentMapper;

    // 成绩权重配置
    private static final BigDecimal THESIS_WEIGHT = new BigDecimal("0.7"); // 论文成绩权重70%
    private static final BigDecimal DEFENSE_WEIGHT = new BigDecimal("0.3"); // 答辩成绩权重30%

    @Override
    @Transactional
    public void scoreThesis(ThesisScoreRequest request) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String currentUserId = UserContextUtil.getCurrentUserId(httpRequest);

        // 验证权限：只有指导该学生的老师才能评分
        validateTeacherPermission(currentUserId, request.getStudentId());

        // 验证学生是否存在
        Student student = studentMapper.selectById(request.getStudentId());
        if (student == null) {
            throw new GradeBusinessException("学生不存在");
        }

        // 创建或更新论文成绩
        Thesis thesis = thesisMapper.selectById(request.getStudentId());
        if (thesis == null) {
            thesis = new Thesis();
            thesis.setStudentId(request.getStudentId());
            thesis.setTeacherId(currentUserId);
            thesis.setThesisScore(request.getThesisScore());
            thesis.setRemarks(request.getRemarks());
            thesisMapper.insert(thesis);
        } else {
            thesis.setThesisScore(request.getThesisScore());
            thesis.setRemarks(request.getRemarks());
            thesisMapper.updateById(thesis);
        }

        // 自动计算最终成绩
        calculateFinalGrade(request.getStudentId());

        log.info("老师 {} 为学生 {} 评定论文成绩: {}", currentUserId, request.getStudentId(), request.getThesisScore());
    }

    @Override
    @Transactional
    public void scoreDefense(DefenseScoreRequest request) {
        // 验证学生是否存在
        Student student = studentMapper.selectById(request.getStudentId());
        if (student == null) {
            throw new GradeBusinessException("学生不存在");
        }

        // 创建或更新答辩成绩
        Defense defense = defenseMapper.selectById(request.getStudentId());
        if (defense == null) {
            defense = new Defense();
            defense.setStudentId(request.getStudentId());
            defense.setScore(request.getScore());
            defense.setComments(request.getComments());
            defenseMapper.insert(defense);
        } else {
            defense.setScore(request.getScore());
            defense.setComments(request.getComments());
            defenseMapper.updateById(defense);
        }

        // 自动计算最终成绩
        calculateFinalGrade(request.getStudentId());

        log.info("为学生 {} 评定答辩成绩: {}", request.getStudentId(), request.getScore());
    }

    @Override
    @Transactional
    public void calculateFinalGrade(String studentId) {
        Thesis thesis = thesisMapper.selectById(studentId);
        Defense defense = defenseMapper.selectById(studentId);

        // 只有当论文成绩和答辩成绩都存在时才计算最终成绩
        if (thesis != null && thesis.getThesisScore() != null &&
                defense != null && defense.getScore() != null) {

            // 计算最终成绩：论文成绩 * 0.7 + 答辩成绩 * 0.3
            BigDecimal finalScore = thesis.getThesisScore()
                    .multiply(THESIS_WEIGHT)
                    .add(defense.getScore().multiply(DEFENSE_WEIGHT))
                    .setScale(2, RoundingMode.HALF_UP);

            // 创建或更新最终成绩
            FinalGrade finalGrade = finalGradeMapper.selectById(studentId);
            if (finalGrade == null) {
                finalGrade = new FinalGrade();
                finalGrade.setStudentId(studentId);
                finalGrade.setFinalScore(finalScore);
                finalGrade.setCalculatedAt(LocalDateTime.now());
                finalGradeMapper.insert(finalGrade);
            } else {
                finalGrade.setFinalScore(finalScore);
                finalGrade.setCalculatedAt(LocalDateTime.now());
                finalGradeMapper.updateById(finalGrade);
            }

            log.info("计算学生 {} 最终成绩: {}", studentId, finalScore);
        }
    }

    @Override
    @Transactional
    public void batchCalculateFinalGrades(String teacherId) {
        // 获取该老师指导的所有学生
        LambdaQueryWrapper<TopicSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TopicSelection::getTeacherId, teacherId);
        List<TopicSelection> selections = topicSelectionMapper.selectList(wrapper);

        for (TopicSelection selection : selections) {
            calculateFinalGrade(selection.getStudentId());
        }

        log.info("批量计算老师 {} 指导学生的最终成绩，共 {} 名学生", teacherId, selections.size());
    }

    @Override
    @Transactional
    public void batchScoreThesis(List<ThesisScoreRequest> requests) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String currentUserId = UserContextUtil.getCurrentUserId(httpRequest);

        // 验证所有请求中的学生都是该老师指导的
        for (ThesisScoreRequest request : requests) {
            validateTeacherPermission(currentUserId, request.getStudentId());
        }

        // 批量处理评分
        for (ThesisScoreRequest request : requests) {
            scoreThesis(request);  // 复用单个评分逻辑
        }

        log.info("老师 {} 批量处理了 {} 个论文评分", currentUserId, requests.size());
    }

    @Override
    @Transactional
    public void batchScoreDefense(List<DefenseScoreRequest> requests) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String currentUserId = UserContextUtil.getCurrentUserId(httpRequest);

        // 验证权限：确保老师只能为自己指导的学生评分
        for (DefenseScoreRequest request : requests) {
            validateTeacherPermission(currentUserId, request.getStudentId());
        }

        // 批量评分
        for (DefenseScoreRequest request : requests) {
            scoreDefense(request);  // 复用已有的评分逻辑
        }

        log.info("老师 {} 批量处理了 {} 个答辩评分", currentUserId, requests.size());
    }







    @Override
    public IPage<StudentGradeResponse> queryStudentGrades(GradeQueryRequest request) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String currentUserId = UserContextUtil.getCurrentUserId(httpRequest);
        String userRole = UserContextUtil.getCurrentUserRole(httpRequest);

        // 如果是老师，只能查看自己指导的学生成绩
        if ("teacher".equals(userRole)) {
            request.setTeacherId(currentUserId);
        }
        // 如果是学生，只能查看自己的成绩
        else if ("student".equals(userRole)) {
            request.setStudentId(currentUserId);
        }

        Page<StudentGradeResponse> page = new Page<>(request.getPageNum(), request.getPageSize());
        return finalGradeMapper.selectStudentGradesPage(page, request);
    }

    @Override
    public StudentGradeResponse getStudentGradeDetail(String studentId) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String currentUserId = UserContextUtil.getCurrentUserId(httpRequest);
        String userRole = UserContextUtil.getCurrentUserRole(httpRequest);

        // 权限验证
        if ("student".equals(userRole) && !currentUserId.equals(studentId)) {
            throw new GradeBusinessException("无权查看其他学生成绩");
        }

        if ("teacher".equals(userRole)) {
            validateTeacherPermission(currentUserId, studentId);
        }

        StudentGradeResponse response = finalGradeMapper.selectStudentGradeDetail(studentId);
        if (response == null) {
            throw new GradeBusinessException("学生成绩信息不存在");
        }

        return response;
    }

    @Override
    public GradeStatisticsResponse getGradeStatistics(String teacherId) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String currentUserId = UserContextUtil.getCurrentUserId(httpRequest);
        String userRole = UserContextUtil.getCurrentUserRole(httpRequest);

        // 如果是老师，只能查看自己指导学生的统计
        if ("teacher".equals(userRole)) {
            teacherId = currentUserId;
        }

        return finalGradeMapper.selectGradeStatistics(teacherId);
    }

    @Override
    public Thesis getThesisScore(String studentId) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String currentUserId = UserContextUtil.getCurrentUserId(httpRequest);
        String userRole = UserContextUtil.getCurrentUserRole(httpRequest);

        // 权限验证
        if ("student".equals(userRole) && !currentUserId.equals(studentId)) {
            throw new GradeBusinessException("无权查看其他学生成绩");
        }

        if ("teacher".equals(userRole)) {
            validateTeacherPermission(currentUserId, studentId);
        }

        return thesisMapper.selectById(studentId);
    }

    @Override
    public Defense getDefenseScore(String studentId) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String currentUserId = UserContextUtil.getCurrentUserId(httpRequest);
        String userRole = UserContextUtil.getCurrentUserRole(httpRequest);

        // 权限验证
        if ("student".equals(userRole) && !currentUserId.equals(studentId)) {
            throw new GradeBusinessException("无权查看其他学生成绩");
        }

        return defenseMapper.selectById(studentId);
    }

    @Override
    public FinalGrade getFinalGrade(String studentId) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String currentUserId = UserContextUtil.getCurrentUserId(httpRequest);
        String userRole = UserContextUtil.getCurrentUserRole(httpRequest);

        // 权限验证
        if ("student".equals(userRole) && !currentUserId.equals(studentId)) {
            throw new GradeBusinessException("无权查看其他学生成绩");
        }

        if ("teacher".equals(userRole)) {
            validateTeacherPermission(currentUserId, studentId);
        }

        return finalGradeMapper.selectById(studentId);
    }

    /**
     * 验证老师权限：只有指导该学生的老师才能操作
     */
    private void validateTeacherPermission(String teacherId, String studentId) {
        LambdaQueryWrapper<TopicSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TopicSelection::getStudentId, studentId)
                .eq(TopicSelection::getTeacherId, teacherId);

        TopicSelection selection = topicSelectionMapper.selectOne(wrapper);
        if (selection == null) {
            throw new GradeBusinessException("无权限操作该学生成绩");
        }
    }

    @Override
    public byte[] exportGrades(GradeExportRequest request) {
        List<StudentGradeResponse> gradeList = finalGradeMapper.selectStudentGrades(request.getStudentIds());

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("学生成绩");

            // 创建表头
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("学生ID");
            headerRow.createCell(1).setCellValue("姓名");
            headerRow.createCell(2).setCellValue("论文成绩");
            headerRow.createCell(3).setCellValue("答辩成绩");
            headerRow.createCell(4).setCellValue("最终成绩");

            // 填充数据
            int rowNum = 1;
            for (StudentGradeResponse grade : gradeList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(grade.getStudentId());
                row.createCell(1).setCellValue(grade.getStudentName());
                row.createCell(2).setCellValue(grade.getThesisScore() != null ? grade.getThesisScore().doubleValue() : 0);
                row.createCell(3).setCellValue(grade.getDefenseScore() != null ? grade.getDefenseScore().doubleValue() : 0);
                row.createCell(4).setCellValue(grade.getFinalScore() != null ? grade.getFinalScore().doubleValue() : 0);
            }

            // 写入字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new GradeBusinessException("成绩导出失败", e);
        }
    }


    @Override
    public Map<String, Object> analyzeGrades(String teacherId) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String currentUserId = UserContextUtil.getCurrentUserId(httpRequest);
        String userRole = UserContextUtil.getCurrentUserRole(httpRequest);

        // 如果是老师，只能查看自己指导学生的统计
        if ("teacher".equals(userRole)) {
            teacherId = currentUserId;
        }

        // 获取成绩统计数据
        return finalGradeMapper.selectGradeAnalysis(teacherId);

    }



}