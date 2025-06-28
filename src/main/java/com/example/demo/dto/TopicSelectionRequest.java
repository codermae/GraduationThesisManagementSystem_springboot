package com.example.demo.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class TopicSelectionRequest {
    @NotBlank(message = "题目ID不能为空")
    private String topicId;
}
