// GradeController.java
package com.example.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.common.Result;
import com.example.demo.dto.*;
import com.example.demo.entity.Defense;
import com.example.demo.entity.FinalGrade;
import com.example.demo.entity.Thesis;
import com.example.demo.service.GradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;


import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/grade")
@RequiredArgsConstructor
@Validated
public class GradeController {

    private final GradeService gradeService;

    /**s
     * 论文评分
     */
    @PostMapping("/thesis/score")
    public Result<Void> scoreThesis(@Valid @RequestBody ThesisScoreRequest request) {
        gradeService.scoreThesis(request);
        return Result.success();
    }

    /**
     * 答辩评分
     */
    @PostMapping("/defense/score")
    public Result<Void> scoreDefense(@Valid @RequestBody DefenseScoreRequest request) {
        gradeService.scoreDefense(request);
        return Result.success();
    }

    /**
     * 计算学生最终成绩
     */
    @PostMapping("/final/calculate/{studentId}")
    public Result<Void> calculateFinalGrade(@PathVariable String studentId) {
        gradeService.calculateFinalGrade(studentId);
        return Result.success();
    }

    /**
     * 批量计算最终成绩（老师）
     */
    @PostMapping("/final/batch-calculate")
    public Result<Void> batchCalculateFinalGrades() {
        // 当前用户ID将在Service层获取
        gradeService.batchCalculateFinalGrades(null);
        return Result.success();
    }

    /**
     * 分页查询学生成绩
     */
    @PostMapping("/list")
    public Result<IPage<StudentGradeResponse>> queryStudentGrades(@Valid @RequestBody GradeQueryRequest request) {
        IPage<StudentGradeResponse> result = gradeService.queryStudentGrades(request);
        return Result.success(result);
    }

    /**
     * 获取学生详细成绩
     */
    @GetMapping("/detail/{studentId}")
    public Result<StudentGradeResponse> getStudentGradeDetail(@PathVariable String studentId) {
        StudentGradeResponse result = gradeService.getStudentGradeDetail(studentId);
        return Result.success(result);
    }

    /**
     * 获取成绩统计信息
     */
    @GetMapping("/statistics")
    public Result<GradeStatisticsResponse> getGradeStatistics(@RequestParam(required = false) String teacherId) {
        GradeStatisticsResponse result = gradeService.getGradeStatistics(teacherId);
        return Result.success(result);
    }

    /**
     * 获取论文成绩
     */
    @GetMapping("/thesis/{studentId}")
    public Result<Thesis> getThesisScore(@PathVariable String studentId) {
        Thesis result = gradeService.getThesisScore(studentId);
        return Result.success(result);
    }

    /**
     * 获取答辩成绩
     */
    @GetMapping("/defense/{studentId}")
    public Result<Defense> getDefenseScore(@PathVariable String studentId) {
        Defense result = gradeService.getDefenseScore(studentId);
        return Result.success(result);
    }

    /**
     * 获取最终成绩
     */
    @GetMapping("/final/{studentId}")
    public Result<FinalGrade> getFinalGrade(@PathVariable String studentId) {
        FinalGrade result = gradeService.getFinalGrade(studentId);
        return Result.success(result);
    }

    @PostMapping("/thesis/batch-score")
    public Result<Void> batchScoreThesis(@Valid @RequestBody GradeBatchRequest request) {
        gradeService.batchScoreThesis(request.getThesisScores());
        return Result.success();
    }

    @PostMapping("/defense/batch-score")
    public Result<Void> batchScoreDefense(@Valid @RequestBody GradeBatchRequest request) {
        gradeService.batchScoreDefense(request.getDefenseScores());
        return Result.success();
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> exportGrades(@Valid @RequestBody GradeExportRequest request) {
        byte[] data = gradeService.exportGrades(request);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "grades." + request.getFormat());
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    @GetMapping("/analyze")
    public Result<Map<String, Object>> analyzeGrades(@RequestParam(required = false) String teacherId) {
        Map<String, Object> result = gradeService.analyzeGrades(teacherId);
        return Result.success(result);
    }
}