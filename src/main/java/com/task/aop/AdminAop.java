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

/**
 * AOP, 管理员认证的具体逻辑
 */
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
        String token = request.getHeader(ProjectConfig.HEAD_TOKEN);//获得 Head 的 token
        User user = null;
        if (TextUtils.isEmpty(token)) {//token 为空
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            user = userRepository.findByToken(token);
            if (null == user || !user.isAdmin()) {//token 找不到用户,或用户不是管理员
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
        injectUserObject(user, args, token);
        return joinPoint.proceed(args);
    }

    private void injectUserObject(User asUser, Object[] args, Object token) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof User) {
                args[i] = asUser;
                break;
            }
        }
    }
}