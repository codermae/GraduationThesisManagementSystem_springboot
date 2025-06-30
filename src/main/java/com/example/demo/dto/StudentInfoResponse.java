package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentInfoResponse {
    private String studentId;
    private String studentName;
    private String className;
    private String major;

    // 选题相关信息
    private String topicId;
    private String topicTitle;
    private LocalDate selectionDate;

    // 论文相关信息
    private Double thesisScore;
    private String thesisRemarks;

    // 答辩相关信息
    private Double defenseScore;
    private String defenseComments;

    // 最终成绩
    private Double finalScore;

    // 文件统计信息
    private Integer fileCount;
    private Long totalFileSize;
}