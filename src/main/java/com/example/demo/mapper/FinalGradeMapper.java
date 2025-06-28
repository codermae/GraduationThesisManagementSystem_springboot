// FinalGradeMapper.java
package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.FinalGrade;
import com.example.demo.dto.StudentGradeResponse;
import com.example.demo.dto.GradeQueryRequest;
import com.example.demo.dto.GradeStatisticsResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;
import java.util.List;

@Mapper
public interface FinalGradeMapper extends BaseMapper<FinalGrade> {

    /**
     * 分页查询学生成绩详情
     */
    IPage<StudentGradeResponse> selectStudentGradesPage(
            Page<?> page,
            @Param("request") GradeQueryRequest request
    );

    /**
     * 获取成绩统计信息
     */
    GradeStatisticsResponse selectGradeStatistics(@Param("teacherId") String teacherId);

    /**
     * 获取学生详细成绩信息
     */
    StudentGradeResponse selectStudentGradeDetail(@Param("studentId") String studentId);

    //导出
    List<StudentGradeResponse> selectStudentGrades(List<String> studentIds);

    /**
     * 成绩分析：平均分、最高分、最低分、分布等
     */
    Map<String, Object> selectGradeAnalysis(String teacherId);
}