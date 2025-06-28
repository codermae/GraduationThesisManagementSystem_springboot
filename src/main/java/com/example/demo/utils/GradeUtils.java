// GradeUtils.java
package com.example.demo.utils;

import com.example.demo.constant.GradeConstants;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class GradeUtils {

    /**
     * 计算最终成绩
     */
    public static BigDecimal calculateFinalScore(BigDecimal thesisScore, BigDecimal defenseScore) {
        if (thesisScore == null || defenseScore == null) {
            return null;
        }

        return thesisScore.multiply(GradeConstants.THESIS_WEIGHT)
                .add(defenseScore.multiply(GradeConstants.DEFENSE_WEIGHT))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 获取成绩等级
     */
    public static String getGradeLevel(BigDecimal score) {
        if (score == null) {
            return "未评分";
        }

        if (score.compareTo(GradeConstants.EXCELLENT_THRESHOLD) >= 0) {
            return "优秀";
        } else if (score.compareTo(GradeConstants.GOOD_THRESHOLD) >= 0) {
            return "良好";
        } else if (score.compareTo(GradeConstants.PASS_THRESHOLD) >= 0) {
            return "及格";
        } else {
            return "不及格";
        }
    }

    /**
     * 验证成绩范围
     */
    public static boolean isValidScore(BigDecimal score) {
        return score != null &&
                score.compareTo(GradeConstants.MIN_SCORE) >= 0 &&
                score.compareTo(GradeConstants.MAX_SCORE) <= 0;
    }
}