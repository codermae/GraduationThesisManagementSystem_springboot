// Thesis.java - 论文成绩实体类
package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("Thesis")
public class Thesis {
    @TableId
    private String studentId;
    private String teacherId;
    private BigDecimal thesisScore;
    private String remarks;
}
