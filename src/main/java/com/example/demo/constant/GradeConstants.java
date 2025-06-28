// GradeConstants.java
package com.example.demo.constant;

import java.math.BigDecimal;

public class GradeConstants {

    // 成绩等级
    public static final BigDecimal EXCELLENT_THRESHOLD = new BigDecimal("90"); // 优秀
    public static final BigDecimal GOOD_THRESHOLD = new BigDecimal("80"); // 良好
    public static final BigDecimal PASS_THRESHOLD = new BigDecimal("60"); // 及格

    // 成绩权重
    public static final BigDecimal THESIS_WEIGHT = new BigDecimal("0.7"); // 论文权重70%
    public static final BigDecimal DEFENSE_WEIGHT = new BigDecimal("0.3"); // 答辩权重30%

    // 成绩范围
    public static final BigDecimal MIN_SCORE = BigDecimal.ZERO;
    public static final BigDecimal MAX_SCORE = new BigDecimal("100");

    // 错误信息
    public static final String STUDENT_NOT_FOUND = "学生不存在";
    public static final String PERMISSION_DENIED = "无权限操作该学生成绩";
    public static final String GRADE_NOT_FOUND = "成绩信息不存在";
    public static final String INVALID_SCORE_RANGE = "成绩必须在0-100之间";
}