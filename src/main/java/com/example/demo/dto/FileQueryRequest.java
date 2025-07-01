// FileQueryRequest.java - 文件查询请求
package com.example.demo.dto;

import lombok.Data;
import com.example.demo.common.PageRequest;

@Data
public class FileQueryRequest extends PageRequest {

    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String studentId;
    private String teacherId;  // 新增：老师ID
    private String fileName;
    private String fileCategory;
    private String fileType;
    private String uploadTimeStart;
    private String uploadTimeEnd;
}