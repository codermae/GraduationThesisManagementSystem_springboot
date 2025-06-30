// FileUploadMapper.java
package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.FileQueryRequest;
import com.example.demo.dto.FileResponse;
import com.example.demo.dto.FileStatisticsResponse;
import com.example.demo.entity.FileUpload;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface FileUploadMapper extends BaseMapper<FileUpload> {

    /**
     * 分页查询文件信息
     */
    IPage<FileResponse> selectFilePageWithStudent(Page<FileResponse> page, @Param("req") FileQueryRequest request);

    /**
     * 根据学生ID查询文件列表
     */
    List<FileResponse> selectFilesByStudentId(@Param("studentId") String studentId);

    /**
     * 根据文件ID和学生ID查询文件（权限验证）
     */
    FileUpload selectByFileIdAndStudentId(@Param("fileId") String fileId, @Param("studentId") String studentId);

    /**
     * 获取文件统计信息
     */
    FileStatisticsResponse selectFileStatistics(@Param("studentId") String studentId);

    /**
     * 查询指定老师指导的所有学生的文件列表
     * @param teacherId 教师ID
     * @return 文件列表
     */
    List<FileResponse> selectFilesByTeacher(@Param("teacherId") String teacherId);


    /**
     * 查询指定老师指导的学生文件统计信息
     * @param teacherId 教师ID
     * @return 统计信息
     */
    Map<String, Object> selectTeacherStudentsFileStatistics(@Param("teacherId") String teacherId);

    /**
     * 批量查询文件（用于批量删除权限验证）
     */
    List<FileUpload> selectBatchByFileIdsAndStudentId(@Param("fileIds") List<String> fileIds, @Param("studentId") String studentId);
}