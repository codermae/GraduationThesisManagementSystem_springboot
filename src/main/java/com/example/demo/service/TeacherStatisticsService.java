// TeacherStatisticsService.java - 服务接口
package com.example.demo.service;

import com.example.demo.dto.TeacherStatisticsDTO;

public interface TeacherStatisticsService {
    /**
     * 获取教师完整统计信息
     * @param teacherId 教师ID
     * @return 统计信息
     */
    TeacherStatisticsDTO getTeacherStatistics(String teacherId);
}
