// TeacherStatisticsDTO.java - 教师统计信息DTO
package com.example.demo.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class TeacherStatisticsDTO {
    // 基本信息
    private String teacherId;
    private String teacherName;
    private String department;

    // 学生相关统计
    private Integer totalStudentCount;
    private List<StudentGradeInfo> studentGrades;

    // 成绩分布统计
    private Integer excellentCount;  // 优秀(90-100)
    private Integer goodCount;       // 良好(80-89)
    private Integer passCount;       // 及格(60-79)
    private Integer failCount;       // 不及格(0-59)

    // 题目相关统计
    private Integer totalTopicCount;

    // 评分相关统计
    private Integer completedGradingCount; // 已完成评分个数
    private Integer pendingGradingCount;   // 未完成评分个数

    // 文件分类统计
    private Map<String, Integer> fileTypeStatistics;

    // 内部类：学生成绩信息
    @Data
    public static class StudentGradeInfo {
        private String studentId;
        private String studentName;
        private String className;
        private String major;
        private String topicTitle;
        private BigDecimal thesisScore;      // 论文成绩
        private BigDecimal defenseScore;     // 答辩成绩
        private BigDecimal finalScore;       // 最终成绩
        private String thesisRemarks;        // 论文评语
        private String defenseComments;      // 答辩评语
        private Boolean hasThesisScore;      // 是否已有论文成绩
        private Boolean hasDefenseScore;     // 是否已有答辩成绩
        private Boolean hasFinalScore;       // 是否已有最终成绩
    }
}
