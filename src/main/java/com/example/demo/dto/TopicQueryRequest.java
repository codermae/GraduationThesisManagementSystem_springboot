package com.example.demo.dto;

import lombok.Data;

@Data
public class TopicQueryRequest {
    private String title;
    private String difficulty;
    private String direction;
    private String teacherId;
    private Boolean isSelected; // 是否已被选择
    private Integer page = 1;
    private Integer size = 10;
}