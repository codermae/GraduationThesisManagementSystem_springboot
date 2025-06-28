// GradeExportRequest.java - 成绩导出请求
package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class GradeExportRequest {
    private String teacherId;
    private String className;
    private String major;
    private List<String> studentIds;
    private String format; // excel, csv, pdf
}