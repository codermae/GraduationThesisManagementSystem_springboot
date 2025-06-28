package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.TopicSelection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TopicSelectionMapper extends BaseMapper<TopicSelection> {

    /**
     * 查询某个题目的选择记录
     */
    @Select("SELECT ts.student_id, ts.topic_id, ts.teacher_id, ts.selection_date, " +
            "s.name as student_name, s.class, s.major " +
            "FROM TopicSelection ts " +
            "JOIN Student s ON ts.student_id = s.student_id " +
            "WHERE ts.topic_id = #{topicId}")
    List<TopicSelection> selectSelectionsByTopic(@Param("topicId") String topicId);

    /**
     * 查询教师指导的学生选题情况
     */
    @Select("SELECT ts.student_id, ts.topic_id, ts.teacher_id, ts.selection_date, " +
            "s.name as student_name, s.class, s.major, " +
            "t.title as topic_title " +
            "FROM TopicSelection ts " +
            "JOIN Student s ON ts.student_id = s.student_id " +
            "JOIN Topic t ON ts.topic_id = t.topic_id " +
            "WHERE ts.teacher_id = #{teacherId}")
    List<TopicSelection> selectSelectionsByTeacher(@Param("teacherId") String teacherId);
}