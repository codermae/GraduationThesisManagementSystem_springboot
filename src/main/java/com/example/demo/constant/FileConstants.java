// FileConstants.java - 文件模块常量
package com.example.demo.constant;

public class FileConstants {

    // 文件类别
    public static final String CATEGORY_PROPOSAL = "PROPOSAL";     // 开题报告
    public static final String CATEGORY_REPORT = "REPORT";         // 实习报告
    public static final String CATEGORY_THESIS = "THESIS";         // 毕业论文
    public static final String CATEGORY_OTHER = "OTHER";           // 其他文档

    // 允许的文件类型
    public static final String[] ALLOWED_FILE_TYPES = {
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "zip"
    };

    // 文件大小限制（50MB）
    public static final long MAX_FILE_SIZE = 50 * 1024 * 1024L;

    // 上传路径
    public static final String UPLOAD_PATH = "/uploads/";

    // 文件类别映射
    public static String getCategoryName(String category) {
        switch (category) {
            case CATEGORY_PROPOSAL: return "PROPOSAL";
            case CATEGORY_REPORT: return "REPORT";
            case CATEGORY_THESIS: return "THESIS";
            case CATEGORY_OTHER: return "OTHER";
            default: return "whatsThis";
        }
    }
}