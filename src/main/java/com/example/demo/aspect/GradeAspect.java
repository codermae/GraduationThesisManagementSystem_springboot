// GradeAspect.java
package com.example.demo.aspect;

import com.example.demo.utils.UserContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Aspect
@Component
public class GradeAspect {

    @Pointcut("execution(* com.example.demo.service.impl.GradeServiceImpl.scoreThesis(..))")
    public void scoreThesisPointcut() {}

    @Pointcut("execution(* com.example.demo.service.impl.GradeServiceImpl.scoreDefense(..))")
    public void scoreDefensePointcut() {}

    @Pointcut("execution(* com.example.demo.service.impl.GradeServiceImpl.calculateFinalGrade(..))")
    public void calculateFinalGradePointcut() {}

    @Before("scoreThesisPointcut()")
    public void beforeScoreThesis(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String currentUserId = UserContextUtil.getCurrentUserId(request);
        Object[] args = joinPoint.getArgs();
        log.info("用户 {} 开始评定论文成绩，参数: {}", currentUserId, args[0]);
    }

    @AfterReturning("scoreThesisPointcut()")
    public void afterScoreThesis(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String currentUserId = UserContextUtil.getCurrentUserId(request);
        log.info("用户 {} 论文评分操作完成", currentUserId);
    }

    @Before("scoreDefensePointcut()")
    public void beforeScoreDefense(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String currentUserId = UserContextUtil.getCurrentUserId(request);
        Object[] args = joinPoint.getArgs();
        log.info("用户 {} 开始评定答辩成绩，参数: {}", currentUserId, args[0]);
    }

    @AfterReturning("scoreDefensePointcut()")
    public void afterScoreDefense(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String currentUserId = UserContextUtil.getCurrentUserId(request);
        log.info("用户 {} 答辩评分操作完成", currentUserId);
    }

    @Before("calculateFinalGradePointcut()")
    public void beforeCalculateFinalGrade(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        log.info("开始计算学生 {} 的最终成绩", args[0]);
    }

    @AfterReturning("calculateFinalGradePointcut()")
    public void afterCalculateFinalGrade(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        log.info("学生 {} 最终成绩计算完成", args[0]);
    }

    @AfterThrowing(pointcut = "scoreThesisPointcut() || scoreDefensePointcut() || calculateFinalGradePointcut()",
            throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Exception ex) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String currentUserId = UserContextUtil.getCurrentUserId(request);
        log.error("用户 {} 执行成绩操作时发生异常: {}", currentUserId, ex.getMessage(), ex);
    }
}