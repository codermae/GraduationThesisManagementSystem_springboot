package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TopicResponse {
    private String topicId;
    private String title;
    private String teacherId;
    private String teacherName;
    private String departmentName;
    private String difficulty;
    private String direction;
    private String content;
    private Boolean isSelected;
    private String selectedStudentId;
    private String selectedStudentName;
    private LocalDateTime selectionDate;
}