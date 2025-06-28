// TopicValidator.java - 自定义验证器
package com.example.demo.validation;

import com.example.demo.constant.TopicConstants;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class TopicValidator {

    private static final List<String> VALID_DIFFICULTIES = Arrays.asList(
            TopicConstants.Difficulty.EASY,
            TopicConstants.Difficulty.MEDIUM,
            TopicConstants.Difficulty.HARD
    );

    private static final List<String> VALID_DIRECTIONS = Arrays.asList(
            TopicConstants.Direction.WEB_DEVELOPMENT,
            TopicConstants.Direction.MOBILE_DEVELOPMENT,
            TopicConstants.Direction.DATA_ANALYSIS,
            TopicConstants.Direction.AI_ML,
            TopicConstants.Direction.SYSTEM_DEVELOPMENT,
            TopicConstants.Direction.DATABASE,
            TopicConstants.Direction.NETWORK_SECURITY,
            TopicConstants.Direction.OTHER
    );

    /**
     * 验证题目难度
     */
    public boolean isValidDifficulty(String difficulty) {
        return difficulty == null || VALID_DIFFICULTIES.contains(difficulty);
    }

    /**
     * 验证研究方向
     */
    public boolean isValidDirection(String direction) {
        return direction == null || VALID_DIRECTIONS.contains(direction);
    }

    /**
     * 验证题目标题长度和内容
     */
    public boolean isValidTitle(String title) {
        return title != null && title.trim().length() > 0 && title.length() <= 200;
    }

    /**
     * 验证题目内容长度
     */
    public boolean isValidContent(String content) {
        return content == null || content.length() <= 5000;
    }
}