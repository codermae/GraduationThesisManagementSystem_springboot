// FileUploadController.java
package com.example.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.common.Result;
import com.example.demo.dto.FileQueryRequest;
import com.example.demo.dto.FileResponse;
import com.example.demo.dto.FileStatisticsResponse;
import com.example.demo.dto.FileUploadRequest;
import com.example.demo.entity.FileUpload;
import com.example.demo.service.FileUploadService;
import com.example.demo.utils.UserContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public Result<FileResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @Valid @ModelAttribute FileUploadRequest request) {

        // 学生只能上传自己的文件
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String currentUserId = UserContextUtil.getCurrentUserId(httpRequest);
        String userRole = UserContextUtil.getCurrentUserRole(httpRequest);

        if ("student".equals(userRole)) {
            request.setStudentId(currentUserId);
        }

        FileResponse response = fileUploadService.uploadFile(file, request);
        return Result.success(response);
    }

    /**
     * 分页查询文件列表（老师查看所有，学生查看自己的）
     */
    @PostMapping("/page")
    public Result<IPage<FileResponse>> queryFilePage(@RequestBody FileQueryRequest request) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String currentUserId = UserContextUtil.getCurrentUserId(httpRequest);
        String userRole = UserContextUtil.getCurrentUserRole(httpRequest);

        // 学生只能查看自己的文件
        if ("student".equals(userRole)) {
            request.setStudentId(currentUserId);
        }

        IPage<FileResponse> result = fileUploadService.queryFilePage(request);
        return Result.success(result);
    }

    /**
     * 查询学生文件列表
     */
    @GetMapping("/student/{studentId}")
    public Result<List<FileResponse>> queryFilesByStudentId(@PathVariable String studentId) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String currentUserId = UserContextUtil.getCurrentUserId(httpRequest);
        String userRole = UserContextUtil.getCurrentUserRole(httpRequest);

        // 学生只能查看自己的文件
        if ("student".equals(userRole) && !studentId.equals(currentUserId)) {
            return Result.error(403, "无权限查看此学生的文件");
        }

        List<FileResponse> files = fileUploadService.queryFilesByStudentId(studentId);
        return Result.success(files);
    }

    /**
     * 获取文件统计信息
     */
    @GetMapping("/statistics")
    public Result<FileStatisticsResponse> getFileStatistics() {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String currentUserId = UserContextUtil.getCurrentUserId(httpRequest);
        String userRole = UserContextUtil.getCurrentUserRole(httpRequest);

        // 学生只能查看自己的统计，老师可以查看所有
        String studentId = "student".equals(userRole) ? currentUserId : null;

        FileStatisticsResponse statistics = fileUploadService.getFileStatistics(studentId);
        return Result.success(statistics);
    }

    /**
     * 获取指定学生的文件统计信息（老师专用）
     */
    @GetMapping("/statistics/{studentId}")
    public Result<FileStatisticsResponse> getStudentFileStatistics(@PathVariable String studentId) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String userRole = UserContextUtil.getCurrentUserRole(httpRequest);

        if (!"teacher".equals(userRole)) {
            return Result.error(403, "只有老师可以查看学生统计信息");
        }

        FileStatisticsResponse statistics = fileUploadService.getFileStatistics(studentId);
        return Result.success(statistics);
    }
    /**
     * 老师查看指定学生的文件列表（按文件类别分组）
     */
    @GetMapping("/teacher/student/{studentId}/files")
    public Result<Map<String, List<FileResponse>>> getStudentFilesByCategory(@PathVariable String studentId, HttpServletRequest request) {
        String currentUserId = UserContextUtil.getCurrentUserId(request);
        String userRole = UserContextUtil.getCurrentUserRole(request);

        if (!"teacher".equals(userRole)) {
            return Result.error(403, "只有老师可以查看学生文件");
        }

        // 验证该学生是否是当前老师指导的学生
        // 这里需要调用TopicService来验证
        // boolean isMyStudent = topicService.isTeacherStudent(currentUserId, studentId);
        // if (!isMyStudent) {
        //     return Result.error(403, "您无权查看该学生的文件");
        // }

        Map<String, List<FileResponse>> filesByCategory = fileUploadService.getStudentFilesByCategory(studentId);
        return Result.success(filesByCategory);
    }

    /**
     * 老师查看所有指导学生的文件汇总
     */
    @GetMapping("/teacher/my-students/files")
    public Result<Map<String, Object>> getMyStudentsFiles(HttpServletRequest request) {
        String currentUserId = UserContextUtil.getCurrentUserId(request);
        String userRole = UserContextUtil.getCurrentUserRole(request);

        if (!"teacher".equals(userRole)) {
            return Result.error(403, "只有老师可以查看指导学生文件");
        }

        Map<String, Object> result = fileUploadService.getTeacherStudentsFiles(currentUserId);
        return Result.success(result);
    }

    /**
     * 下载文件
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String currentUserId = UserContextUtil.getCurrentUserId(httpRequest);
        String userRole = UserContextUtil.getCurrentUserRole(httpRequest);

        FileUpload fileUpload = fileUploadService.downloadFile(fileId, currentUserId, userRole);

        File file = new File(fileUpload.getFilePath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);

        try {
            String encodedFileName = URLEncoder.encode(fileUpload.getOriginalName(), "UTF-8");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + encodedFileName + "\"")
                    .body(resource);
        } catch (UnsupportedEncodingException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{fileId}")
    public Result<Void> deleteFile(@PathVariable String fileId) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String currentUserId = UserContextUtil.getCurrentUserId(httpRequest);
        boolean result = fileUploadService.deleteFile(fileId, currentUserId);
        return result ? Result.success() : Result.error("删除失败");
    }

    /**
     * 批量删除文件
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteFiles(@RequestBody List<String> fileIds) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String currentUserId = UserContextUtil.getCurrentUserId(httpRequest);
        boolean result = fileUploadService.batchDeleteFiles(fileIds, currentUserId);
        return result ? Result.success() : Result.error("批量删除失败");
    }
}