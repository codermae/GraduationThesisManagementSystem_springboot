package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("file_upload")
public class FileUpload {

    @TableId(value = "file_id", type = IdType.ASSIGN_UUID)
    private String fileId;

    @TableField("student_id")
    private String studentId;

    @TableField("file_name")
    private String fileName;

    @TableField("original_name")
    private String originalName;

    @TableField("file_path")
    private String filePath;

    @TableField("file_size")
    private Long fileSize;

    @TableField("file_type")
    private String fileType;

    @TableField("file_category")
    private String fileCategory; // PROPOSAL, REPORT, THESIS, OTHER

    @TableField("upload_time")
    private LocalDateTime uploadTime;

    @TableField("is_deleted")
    @TableLogic
    private Boolean isDeleted;

    @TableField("remarks")
    private String remarks;
}