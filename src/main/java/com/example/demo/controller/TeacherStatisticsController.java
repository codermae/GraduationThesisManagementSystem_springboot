// TeacherStatisticsController.java - 控制器
package com.example.demo.controller;

import com.example.demo.dto.TeacherStatisticsDTO;
import com.example.demo.service.TeacherStatisticsService;
import com.example.demo.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teacher/statistics")
public class TeacherStatisticsController {

    @Autowired
    private TeacherStatisticsService teacherStatisticsService;

    /**
     * 获取指定教师的完整统计信息
     * @param teacherId 教师ID
     * @return 教师统计信息
     */
    @GetMapping("/{teacherId}")
    public Result<TeacherStatisticsDTO> getTeacherStatistics(@PathVariable String teacherId) {
        try {
            TeacherStatisticsDTO statistics = teacherStatisticsService.getTeacherStatistics(teacherId);
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error("获取教师统计信息失败: " + e.getMessage());
        }
    }
}