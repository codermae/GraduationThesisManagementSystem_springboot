package com.example.demo.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class TopicCreateRequest {
    @NotBlank(message = "题目标题不能为空")
    @Size(max = 200, message = "题目标题长度不能超过200字符")
    private String title;

    @Size(max = 20, message = "难度标识长度不能超过20字符")
    private String difficulty;

    @Size(max = 100, message = "研究方向长度不能超过100字符")
    private String direction;

    private String content;
}