package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标识某个方法需要进行公共字段的填充处理
 */
@Target(ElementType.METHOD) //注解加在什么位置
@Retention(RetentionPolicy.RUNTIME) //什么时候被保留
public @interface AutoFill {
    //指定属性、当前数据库操作类型
    OperationType value();
}
