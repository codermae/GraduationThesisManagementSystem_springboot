// GradeConfig.java
package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Configuration
@ConfigurationProperties(prefix = "grade")
public class GradeConfig {

    /**
     * 论文成绩权重
     */
    private BigDecimal thesisWeight = new BigDecimal("0.7");

    /**
     * 答辩成绩权重
     */
    private BigDecimal defenseWeight = new BigDecimal("0.3");

    /**
     * 优秀成绩阈值
     */
    private BigDecimal excellentThreshold = new BigDecimal("90");

    /**
     * 良好成绩阈值
     */
    private BigDecimal goodThreshold = new BigDecimal("80");

    /**
     * 及格成绩阈值
     */
    private BigDecimal passThreshold = new BigDecimal("60");

    /**
     * 是否启用自动计算最终成绩
     */
    private boolean autoCalculate = true;

    /**
     * 备注最大长度
     */
    private int maxRemarksLength = 500;
}