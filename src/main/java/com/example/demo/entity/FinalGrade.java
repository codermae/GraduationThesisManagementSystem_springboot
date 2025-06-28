// FinalGrade.java - 最终成绩实体类
package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("FinalGrade")
public class FinalGrade {
    @TableId
    private String studentId;
    private BigDecimal finalScore;
    private LocalDateTime calculatedAt;
}