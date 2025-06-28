// GradeValidator.java
package com.example.demo.validator;

import com.example.demo.constant.GradeConstants;
import com.example.demo.exception.GradeBusinessException;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class GradeValidator {

    /**
     * 验证成绩范围
     */
    public void validateScore(BigDecimal score, String scoreType) {
        if (score == null) {
            throw new GradeBusinessException(scoreType + "不能为空");
        }

        if (score.compareTo(GradeConstants.MIN_SCORE) < 0 ||
                score.compareTo(GradeConstants.MAX_SCORE) > 0) {
            throw new GradeBusinessException(scoreType + "必须在0-100之间");
        }
    }

    /**
     * 验证学生ID
     */
    public void validateStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new GradeBusinessException("学生ID不能为空");
        }
    }

    /**
     * 验证备注长度
     */
    public void validateRemarks(String remarks, int maxLength) {
        if (remarks != null && remarks.length() > maxLength) {
            throw new GradeBusinessException("备注长度不能超过" + maxLength + "字符");
        }
    }
}
