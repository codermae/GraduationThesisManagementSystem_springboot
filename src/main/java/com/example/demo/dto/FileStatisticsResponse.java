// FileStatisticsResponse.java - 文件统计响应
package com.example.demo.dto;

import lombok.Data;

@Data
public class FileStatisticsResponse {

    private Long totalFiles;
    private Long totalSize;
    private Long proposalCount;
    private Long reportCount;
    private Long thesisCount;
    private Long otherCount;
    private String formattedTotalSize;
}