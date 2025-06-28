// GradeQueryRequest.java - 成绩查询请求
package com.example.demo.dto;

import com.example.demo.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class GradeQueryRequest extends PageRequest {
    private String studentId;
    private String studentName;
    private String teacherId;
    private String className;
    private String major;
    private BigDecimal minScore;
    private BigDecimal maxScore;
}
