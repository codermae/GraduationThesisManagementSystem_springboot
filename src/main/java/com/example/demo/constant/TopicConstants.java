package com.example.demo.constant;

public class TopicConstants {

    /**
     * 题目难度常量
     */
    public static final class Difficulty {
        public static final String EASY = "简单";
        public static final String MEDIUM = "中等";
        public static final String HARD = "困难";
    }

    /**
     * 研究方向常量
     */
    public static final class Direction {
        public static final String WEB_DEVELOPMENT = "Web开发";
        public static final String MOBILE_DEVELOPMENT = "移动开发";
        public static final String DATA_ANALYSIS = "数据分析";
        public static final String AI_ML = "人工智能/机器学习";
        public static final String SYSTEM_DEVELOPMENT = "系统开发";
        public static final String DATABASE = "数据库";
        public static final String NETWORK_SECURITY = "网络安全";
        public static final String OTHER = "其他";
    }

    /**
     * 选题状态常量
     */
    public static final class SelectionStatus {
        public static final String AVAILABLE = "可选择";
        public static final String SELECTED = "已选择";
        public static final String UNAVAILABLE = "不可选择";
    }

    /**
     * 业务错误消息
     */
    public static final class ErrorMessages {
        public static final String TOPIC_NOT_FOUND = "题目不存在";
        public static final String TOPIC_ALREADY_SELECTED = "题目已被选择";
        public static final String STUDENT_ALREADY_SELECTED = "学生已选择题目";
        public static final String PERMISSION_DENIED = "权限不足";
        public static final String TOPIC_CANNOT_MODIFY = "题目已被选择，无法修改";
        public static final String TOPIC_CANNOT_DELETE = "题目已被选择，无法删除";
    }
}