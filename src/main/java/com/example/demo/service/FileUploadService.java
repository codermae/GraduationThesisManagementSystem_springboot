// FileUploadService.java
package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.dto.*;
import com.example.demo.entity.FileUpload;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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
     * 获取指定学生的文件列表（按类别分组）
     * @param studentId 学生ID
     * @return 按文件类别分组的文件列表
     */
    Map<String, List<FileResponse>> getStudentFilesByCategory(String studentId);


    /**
     * 获取老师指导的所有学生的文件汇总
     * @param teacherId 教师ID
     * @return 文件汇总信息
     */
    Map<String, Object> getTeacherStudentsFiles(String teacherId);

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

    /**
     * 验证老师是否有权限查看指定学生的文件
     * @param teacherId 老师ID
     * @param studentId 学生ID
     * @return 是否有权限
     */
    boolean validateTeacherStudentPermission(String teacherId, String studentId);

}