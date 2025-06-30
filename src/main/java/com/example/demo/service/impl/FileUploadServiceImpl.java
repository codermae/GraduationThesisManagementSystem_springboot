package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.constant.FileConstants;
import com.example.demo.dto.*;
import com.example.demo.entity.FileUpload;
import com.example.demo.exception.FileBusinessException;
import com.example.demo.mapper.FileUploadMapper;
import com.example.demo.service.FileUploadService;
import com.example.demo.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl extends ServiceImpl<FileUploadMapper, FileUpload> implements FileUploadService {

    private final FileUploadMapper fileUploadMapper;

    @Value("${file.upload.path:./uploads/}")
    private String uploadPath;

    @Value("${file.download.url-prefix:/api/files/download/}")
    private String downloadUrlPrefix;

    @Override
    @Transactional
    public FileResponse uploadFile(MultipartFile file, FileUploadRequest request) {
        // 1. 验证文件
        validateFile(file);

        // 2. 生成文件信息
        String originalName = file.getOriginalFilename();
        String fileExtension = FileUtils.getFileExtension(originalName);
        String fileName = generateFileName(fileExtension);
        String filePath = generateFilePath(request.getStudentId(), fileName);

        // 3. 保存文件到磁盘
        saveFileToDisk(file, filePath);

        // 4. 保存文件信息到数据库
        FileUpload fileUpload = new FileUpload();
        fileUpload.setStudentId(request.getStudentId());
        fileUpload.setFileName(fileName);
        fileUpload.setOriginalName(originalName);
        fileUpload.setFilePath(filePath);
        fileUpload.setFileSize(file.getSize());
        fileUpload.setFileType(fileExtension);
        fileUpload.setFileCategory(request.getFileCategory());
        fileUpload.setUploadTime(LocalDateTime.now());
        fileUpload.setRemarks(request.getRemarks());
        fileUpload.setIsDeleted(false);

        save(fileUpload);

        // 5. 构建响应
        return buildFileResponse(fileUpload);
    }

    @Override
    public IPage<FileResponse> queryFilePage(FileQueryRequest request) {
        Page<FileResponse> page = new Page<>(request.getPageNum(), request.getPageSize());
        return fileUploadMapper.selectFilePageWithStudent(page, request);
    }

    @Override
    public List<FileResponse> queryFilesByStudentId(String studentId) {
        return fileUploadMapper.selectFilesByStudentId(studentId);
    }

    @Override
    public FileUpload downloadFile(String fileId, String currentUserId, String userRole) {
        FileUpload fileUpload = getById(fileId);
        if (fileUpload == null) {
            throw new FileBusinessException("文件不存在");
        }

        // 权限检查：学生只能下载自己的文件，老师可以下载所有文件
        if ("student".equals(userRole) && !fileUpload.getStudentId().equals(currentUserId)) {
            throw new FileBusinessException("无权限下载此文件");
        }

        return fileUpload;
    }

    @Override
    @Transactional
    public boolean deleteFile(String fileId, String currentUserId) {
        FileUpload fileUpload = fileUploadMapper.selectByFileIdAndStudentId(fileId, currentUserId);
        if (fileUpload == null) {
            throw new FileBusinessException("文件不存在或无权限删除");
        }

        // 逻辑删除、软删除
        boolean result = removeById(fileId);

        // 删除磁盘文件
        if (result) {
            deletePhysicalFile(fileUpload.getFilePath());
        }

        return result;
    }

    @Override
    public FileStatisticsResponse getFileStatistics(String studentId) {
        return fileUploadMapper.selectFileStatistics(studentId);
    }

    @Override
    @Transactional
    public boolean batchDeleteFiles(List<String> fileIds, String currentUserId) {
        if (fileIds == null || fileIds.isEmpty()) {
            return true;
        }

        // 验证权限：查询这些文件是否都属于当前用户
        List<FileUpload> files = fileUploadMapper.selectBatchByFileIdsAndStudentId(fileIds, currentUserId);
        if (files.size() != fileIds.size()) {
            throw new FileBusinessException("存在无权限删除的文件");
        }

        // 批量逻辑删除
        boolean result = removeByIds(fileIds);

        // 删除物理文件
        if (result) {
            for (FileUpload file : files) {
                deletePhysicalFile(file.getFilePath());
            }
        }

        return result;
    }

    @Override
    public void validateFileType(String fileName) {
        String extension = FileUtils.getFileExtension(fileName);
        boolean isAllowed = false;
        for (String allowedType : FileConstants.ALLOWED_FILE_TYPES) {
            if (allowedType.equalsIgnoreCase(extension)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            throw new FileBusinessException(400, "不支持的文件类型：" + extension);
        }
    }

    @Override
    public void validateFileSize(long fileSize) {
        if (fileSize > FileConstants.MAX_FILE_SIZE) {
            throw new FileBusinessException(400, "文件大小超过限制，最大支持50MB");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileBusinessException(400, "文件不能为空");
        }

        validateFileSize(file.getSize());
        validateFileType(file.getOriginalFilename());
    }

    private String generateFileName(String extension) {
        return UUID.randomUUID().toString().replace("-", "") + "." + extension;
    }

    private String generateFilePath(String studentId, String fileName) {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return uploadPath + dateStr + "/" + studentId + "/" + fileName;
    }

    private void saveFileToDisk(MultipartFile file, String filePath) {
        try {
            File destFile = new File(filePath);
            File parentDir = destFile.getParentFile();

            log.info("准备保存文件至路径: {}", destFile.getAbsolutePath());

            if (!parentDir.exists()) {
                boolean success = parentDir.mkdirs();
                if (!success) {
                    log.warn("创建目录失败，请检查权限和路径合法性: {}", parentDir.getAbsolutePath());
                    throw new FileBusinessException("无法创建文件存储目录");
                }
            }

            file.transferTo(destFile);

        } catch (IOException e) {
            log.error("文件保存失败: {}", e.getMessage(), e);
            throw new FileBusinessException("文件保存失败");
        }
    }



    private void deletePhysicalFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            log.warn("删除物理文件失败: {}", e.getMessage());
        }
    }

    private FileResponse buildFileResponse(FileUpload fileUpload) {
        FileResponse response = new FileResponse();
        response.setFileId(fileUpload.getFileId());
        response.setStudentId(fileUpload.getStudentId());
        response.setFileName(fileUpload.getFileName());
        response.setOriginalName(fileUpload.getOriginalName());
        response.setFileSize(fileUpload.getFileSize());
        response.setFileType(fileUpload.getFileType());
        response.setFileCategory(fileUpload.getFileCategory());
        response.setFileCategoryName(FileConstants.getCategoryName(fileUpload.getFileCategory()));
        response.setUploadTime(fileUpload.getUploadTime());
        response.setRemarks(fileUpload.getRemarks());
        response.setDownloadUrl(downloadUrlPrefix + fileUpload.getFileId());
        return response;
    }

    @Override
    public Map<String, List<FileResponse>> getStudentFilesByCategory(String studentId) {
        List<FileResponse> files = fileUploadMapper.selectFilesByStudentId(studentId);

        // 按文件类别分组
        Map<String, List<FileResponse>> filesByCategory = files.stream()
                .collect(Collectors.groupingBy(file ->
                        FileConstants.getCategoryName(file.getFileCategory())
                ));

        return filesByCategory;
    }

    @Override
    public Map<String, Object> getTeacherStudentsFiles(String teacherId) {
        // 获取老师指导的所有学生文件
        List<FileResponse> allFiles = fileUploadMapper.selectFilesByTeacher(teacherId);

        // 统计信息
        Map<String, Object> result = new HashMap<>();
        result.put("totalFiles", allFiles.size());
        result.put("totalSize", allFiles.stream().mapToLong(FileResponse::getFileSize).sum());

        // 按学生分组
        Map<String, List<FileResponse>> filesByStudent = allFiles.stream()
                .collect(Collectors.groupingBy(FileResponse::getStudentId));

        // 按文件类别统计
        Map<String, Long> filesByCategory = allFiles.stream()
                .collect(Collectors.groupingBy(
                        file -> FileConstants.getCategoryName(file.getFileCategory()),
                        Collectors.counting()
                ));

        result.put("filesByStudent", filesByStudent);
        result.put("filesByCategory", filesByCategory);

        return result;
    }
}