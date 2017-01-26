package com.task.aop;

import com.task.bean.User;
import com.task.config.ProjectConfig;
import com.task.repository.UserRepository;
import com.task.utils.TextUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Order(10)
@Configuration
public class AdminAop {
    @Autowired
    UserRepository userRepository;

    @Autowired
    HttpServletRequest request;

    @Pointcut("execution(* com.task.controller.*.*(..))&&" + "@annotation(com.task.annotation.AdminValid)")
    public void adminPointCut() {
    }


    @Around(value = "adminPointCut()")
    public Object addTokenToMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String token = request.getHeader(ProjectConfig.HEAD_TOKEN);
        if (TextUtils.isEmpty(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = userRepository.findByToken(token);
        if (null == user || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return joinPoint.proceed(args);
    }
}