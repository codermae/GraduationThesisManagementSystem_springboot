// FileUploadService.java
package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.dto.*;
import com.example.demo.entity.FileUpload;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileUploadService extends IService<FileUpload> {

    /**
     * 上传文件
     */
    FileResponse uploadFile(MultipartFile file, FileUploadRequest request);

    /**
     * 分页查询文件
     */
    IPage<FileResponse> queryFilePage(FileQueryRequest request);

    /**
     * 根据学生ID查询文件列表
     */
    List<FileResponse> queryFilesByStudentId(String studentId);

    /**
     * 下载文件
     */
    FileUpload downloadFile(String fileId, String currentUserId, String userRole);

    /**
     * 删除文件
     */
    boolean deleteFile(String fileId, String currentUserId);

    /**
     * 获取文件统计信息
     */
    FileStatisticsResponse getFileStatistics(String studentId);

    /**
     * 批量删除文件
     */
    boolean batchDeleteFiles(List<String> fileIds, String currentUserId);

    /**
     * 验证文件类型
     */
    void validateFileType(String fileName);

    /**
     * 验证文件大小
     */
    void validateFileSize(long fileSize);
}