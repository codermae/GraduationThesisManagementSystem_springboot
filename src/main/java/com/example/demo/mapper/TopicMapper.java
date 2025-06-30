package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.StudentInfoResponse;
import com.example.demo.dto.TopicQueryRequest;
import com.example.demo.dto.TopicResponse;
import com.example.demo.entity.Topic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TopicMapper extends BaseMapper<Topic> {

    /**
     * 分页查询题目列表（带关联信息）
     */
    IPage<TopicResponse> selectTopicPage(Page<TopicResponse> page, @Param("req") TopicQueryRequest request);

    /**
     * 根据题目ID查询详细信息
     */
    TopicResponse selectTopicDetail(@Param("topicId") String topicId);

    /**
     * 查询教师发布的题目列表
     */
    List<TopicResponse> selectTopicsByTeacher(@Param("teacherId") String teacherId);

    /**
     * 检查题目是否已被选择
     */
    @Select("SELECT COUNT(*) FROM TopicSelection WHERE topic_id = #{topicId}")
    int countTopicSelections(@Param("topicId") String topicId);

    /**
     * 查询学生已选择的题目
     */
    @Select("SELECT ts.topic_id, ts.teacher_id, ts.selection_date, " +
            "t.title, t.difficulty, t.direction, t.content, " +
            "teacher.name as teacher_name, teacher.department as department_name " +
            "FROM TopicSelection ts " +
            "JOIN Topic t ON ts.topic_id = t.topic_id " +
            "JOIN Teacher teacher ON ts.teacher_id = teacher.teacher_id " +
            "WHERE ts.student_id = #{studentId}")
    TopicResponse selectStudentSelectedTopic(@Param("studentId") String studentId);

    /**
     * 查询指定教师名下的学生信息
     * @param teacherId 教师ID
     * @return 学生信息列表
     */
    List<StudentInfoResponse> selectStudentsInfoByTeacher(@Param("teacherId") String teacherId);

    /**
     * 查询指定学生的详细信息
     * @param studentId 学生ID
     * @return 学生详细信息
     */
    StudentInfoResponse selectStudentDetailInfo(@Param("studentId") String studentId);

    /**
     * 验证学生是否是指定老师的学生
     * @param teacherId 教师ID
     * @param studentId 学生ID
     * @return 记录数
     */
    int countTeacherStudent(@Param("teacherId") String teacherId, @Param("studentId") String studentId);

}