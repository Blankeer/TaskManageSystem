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
public class TokenAop {
    @Autowired
    UserRepository userRepository;

    @Autowired
    HttpServletRequest request;

    @Pointcut("execution(* com.task.controller.*.*(..))&&" + "@annotation(com.task.annotation.TokenValid)")
    public void tokenPointCut() {
    }


    @Around(value = "tokenPointCut()")
    public Object addTokenToMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String token = request.getHeader(ProjectConfig.HEAD_TOKEN);
        User user = null;
        if (TextUtils.isEmpty(token)) {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            // TODO: 2017/3/18 test user
            user = userRepository.findOne(1);//test
        } else {
            user = userRepository.findByToken(token);
            if (null == user) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
        injectUserObject(user, args, token);//注射参数user
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