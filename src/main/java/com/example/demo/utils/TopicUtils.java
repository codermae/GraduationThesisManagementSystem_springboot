// TopicUtils.java - 选题工具类
package com.example.demo.utils;

import com.example.demo.constant.TopicConstants;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.regex.Pattern;

public class TopicUtils {

    private static final Pattern TOPIC_ID_PATTERN = Pattern.compile("^T\\d{13}[A-Za-z0-9]{8}$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 生成题目ID
     * 格式: T + 13位时间戳 + 8位随机字符
     */
    public static String generateTopicId() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "T" + timestamp + randomStr;
    }

    /**
     * 验证题目ID格式
     */
    public static boolean isValidTopicId(String topicId) {
        return StringUtils.hasText(topicId) && TOPIC_ID_PATTERN.matcher(topicId).matches();
    }

    /**
     * 格式化选题日期
     */
    public static String formatSelectionDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }

    /**
     * 解析选题日期
     */
    public static LocalDate parseSelectionDate(String dateStr) {
        return StringUtils.hasText(dateStr) ? LocalDate.parse(dateStr, DATE_FORMATTER) : null;
    }

    /**
     * 获取题目状态描述
     */
    public static String getTopicStatusDesc(boolean isSelected) {
        return isSelected ? TopicConstants.SelectionStatus.SELECTED : TopicConstants.SelectionStatus.AVAILABLE;
    }

    /**
     * 验证题目标题
     */
    public static boolean isValidTitle(String title) {
        return StringUtils.hasText(title) && title.trim().length() <= 200;
    }

    /**
     * 验证题目内容
     */
    public static boolean isValidContent(String content) {
        return content == null || content.length() <= 5000;
    }

    /**
     * 清理题目内容（去除多余空格、换行等）
     */
    public static String cleanContent(String content) {
        if (!StringUtils.hasText(content)) {
            return content;
        }
        return content.trim().replaceAll("\\s+", " ");
    }

    /**
     * 截取题目内容摘要
     */
    public static String getContentSummary(String content, int maxLength) {
        if (!StringUtils.hasText(content)) {
            return "";
        }

        String cleaned = cleanContent(content);
        if (cleaned.length() <= maxLength) {
            return cleaned;
        }

        return cleaned.substring(0, maxLength) + "...";
    }
}