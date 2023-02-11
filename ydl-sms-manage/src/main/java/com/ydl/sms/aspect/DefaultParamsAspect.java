package com.ydl.sms.aspect;

import com.ydl.context.BaseContextHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 通过切面方式，自定义注解，实现实体基础数据的注入（创建者、创建时间、修改者、修改时间）
 */
@Component //交给spring
@Aspect
@Slf4j
public class DefaultParamsAspect {
    @SneakyThrows
    @Before("@annotation(com.ydl.sms.annotation.DefaultParams)")
    public void beforeEvent(JoinPoint point) {
        // TODO 自动注入基础属性（创建者、创建时间、修改者、修改时间）
        System.out.println("走到切面方法中！");
        Long userId = BaseContextHandler.getUserId();
        Object[] args = point.getArgs();
        for (Object arg : args) {
            Class<?> aClass = arg.getClass();
            Method getId = getMethod(aClass, "getId");
            Object id;
            //避免不是实体类的方法上加上我们的切面
            if (getId != null) {
                id = getId.invoke(arg);

//在这里我们分为有id与没有id的情况 有则（修改时间，修改人）没有则（创建时间，创建人，修改时间，修改人）
            if (id == null) {
                Method setCreateUser = getMethod(aClass, "setCreateUser", String.class);
                setCreateUser.invoke(arg, userId.toString());
                Method setCreateTime = getMethod(aClass, "setCreateTime", LocalDateTime.class);
                setCreateTime.invoke(arg, LocalDateTime.now());
            }
            //其余的都是要执行修改的逻辑的
            Method setUpdateUser = getMethod(aClass, "setUpdateUser", String.class);
            if (setUpdateUser != null) {
                setUpdateUser.invoke(arg, userId.toString());
            }
            Method setUpdateTime = getMethod(aClass, "setUpdateTime", LocalDateTime.class);
            if (setUpdateTime != null) {
                setUpdateTime.invoke(arg, LocalDateTime.now());
            }
        }
        }
    }

    /**
     * 获得方法对象
     *
     * @param classes
     * @param name    方法名
     * @param types   参数类型
     * @return
     */
    private Method getMethod(Class classes, String name, Class... types) {
        try {
            return classes.getMethod(name, types);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
