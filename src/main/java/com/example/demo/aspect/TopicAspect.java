// TopicAspect.java - 选题操作切面（日志记录）
package com.example.demo.aspect;

import com.example.demo.utils.UserContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class TopicAspect {

    /**
     * 选题操作切点
     */
    @Pointcut("execution(* com.example.demo.service.TopicService.selectTopic(..))")
    public void selectTopicPointcut() {}

    /**
     * 取消选题操作切点
     */
    @Pointcut("execution(* com.example.demo.service.TopicService.cancelTopicSelection(..))")
    public void cancelSelectionPointcut() {}

    /**
     * 创建题目操作切点
     */
    @Pointcut("execution(* com.example.demo.service.TopicService.createTopic(..))")
    public void createTopicPointcut() {}

    /**
     * 记录选题操作
     */
    @AfterReturning("selectTopicPointcut()")
    public void logSelectTopic(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String userId = UserContextUtil.getCurrentUserId(request);
        Object[] args = joinPoint.getArgs();
        log.info("学生选题成功 - 学生ID: {}, 参数: {}", userId, args);
    }

    /**
     * 记录取消选题操作
     */
    @AfterReturning("cancelSelectionPointcut()")
    public void logCancelSelection(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String userId = UserContextUtil.getCurrentUserId(request);
        log.info("学生取消选题成功 - 学生ID: {}", userId);
    }

    /**
     * 记录创建题目操作
     */
    @AfterReturning("createTopicPointcut()")
    public void logCreateTopic(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String userId = UserContextUtil.getCurrentUserId(request);
        Object[] args = joinPoint.getArgs();
        log.info("教师创建题目成功 - 教师ID: {}, 参数: {}", userId, args);
    }

    /**
     * 记录异常操作
     */
    @AfterThrowing(pointcut = "selectTopicPointcut() || cancelSelectionPointcut() || createTopicPointcut()",
            throwing = "exception")
    public void logException(JoinPoint joinPoint, Exception exception) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String userId = UserContextUtil.getCurrentUserId(request);
        String methodName = joinPoint.getSignature().getName();
        log.error("选题操作异常 - 用户ID: {}, 方法: {}, 异常: {}", userId, methodName, exception.getMessage());
    }
}
