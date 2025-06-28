// ThesisScoreRequest.java - 论文评分请求
package com.example.demo.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class ThesisScoreRequest {
    @NotBlank(message = "学生ID不能为空")
    private String studentId;

    @NotNull(message = "论文成绩不能为空")
    @DecimalMin(value = "0.00", message = "成绩不能小于0")
    @DecimalMax(value = "100.00", message = "成绩不能大于100")
    @Digits(integer = 3, fraction = 2, message = "成绩格式不正确")
    private BigDecimal thesisScore;

    @Size(max = 500, message = "备注不能超过500字符")
    private String remarks;
}