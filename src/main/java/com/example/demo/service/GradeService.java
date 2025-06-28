// GradeService.java
package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.dto.*;
import com.example.demo.entity.Thesis;
import com.example.demo.entity.Defense;
import com.example.demo.entity.FinalGrade;
import java.util.List;


import java.util.Map;

public interface GradeService {

    /**
     * 论文评分
     */
    void scoreThesis(ThesisScoreRequest request);

    /**
     * 答辩评分
     */
    void scoreDefense(DefenseScoreRequest request);

    /**
     * 计算并更新最终成绩
     */
    void calculateFinalGrade(String studentId);

    /**
     * 批量计算最终成绩
     */
    void batchCalculateFinalGrades(String teacherId);

    /**
     * 分页查询学生成绩
     */
    IPage<StudentGradeResponse> queryStudentGrades(GradeQueryRequest request);

    /**
     * 获取学生详细成绩
     */
    StudentGradeResponse getStudentGradeDetail(String studentId);

    /**
     * 获取成绩统计信息
     */
    GradeStatisticsResponse getGradeStatistics(String teacherId);

    /**
     * 获取论文成绩
     */
    Thesis getThesisScore(String studentId);

    /**
     * 获取答辩成绩
     */
    Defense getDefenseScore(String studentId);

    /**
     * 获取最终成绩
     */
    FinalGrade getFinalGrade(String studentId);

    // 批量评分
    void batchScoreThesis(List<ThesisScoreRequest> requests);
    void batchScoreDefense(List<DefenseScoreRequest> requests);

    // 导出成绩
    byte[] exportGrades(GradeExportRequest request);

    // 成绩分析
    Map<String, Object> analyzeGrades(String teacherId);
}