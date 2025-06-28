// FileQueryRequest.java - 文件查询请求
package com.example.demo.dto;

import lombok.Data;
import com.example.demo.common.PageRequest;

@Data
public class FileQueryRequest extends PageRequest {

    private String studentId;
    private String fileName;
    private String fileCategory;
    private String fileType;
}