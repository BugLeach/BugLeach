package com.ydl.sms.annotation;

import java.lang.annotation.*;

@Documented //javadoc
@Retention(RetentionPolicy.RUNTIME) //什么时候起作用
@Target(ElementType.METHOD) //放在哪些地方
public @interface DefaultParams {
}



