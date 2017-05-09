package com.task.annotation;

import java.lang.annotation.*;

/**
 * 管理员 token 认证的注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AdminValid {

}