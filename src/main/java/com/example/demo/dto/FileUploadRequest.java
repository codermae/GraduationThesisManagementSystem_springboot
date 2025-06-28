// FileUploadRequest.java - 文件上传请求
package com.example.demo.dto;

import com.example.demo.validation.ValidFileCategory;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class FileUploadRequest {

    @NotBlank(message = "文件类别不能为空")
    @ValidFileCategory
    private String fileCategory;

    @Size(max = 500, message = "备注长度不能超过500字符")
    private String remarks;

    private String studentId; // 由系统自动设置
}