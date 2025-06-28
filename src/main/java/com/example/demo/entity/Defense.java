// Defense.java - 答辩成绩实体类
package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("Defense")
public class Defense {
    @TableId
    private String studentId;
    private BigDecimal score;
    private String comments;
}