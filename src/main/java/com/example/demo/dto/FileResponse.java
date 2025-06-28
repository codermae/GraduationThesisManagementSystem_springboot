// FileResponse.java - 文件响应
package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FileResponse {

    private String fileId;
    private String studentId;
    private String studentName;
    private String fileName;
    private String originalName;
    private Long fileSize;
    private String fileType;
    private String fileCategory;
    private String fileCategoryName;
    private LocalDateTime uploadTime;
    private String remarks;
    private String downloadUrl;
}