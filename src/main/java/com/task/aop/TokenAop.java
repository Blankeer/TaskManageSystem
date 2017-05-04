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
        Object[] args = joinPoint.getArgs();//被调用函数参数
        String token = request.getHeader(ProjectConfig.HEAD_TOKEN);//获取header中的token
        User user = null;
        if (TextUtils.isEmpty(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);//返回401，即没有授权
        } else {
            user = userRepository.findByToken(token);//根据token在数据库中找对应用户
            if (null == user) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);//用户找不到
            }
        }
        injectUserObject(user, args, token);//注射参数user给args中的user参数
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