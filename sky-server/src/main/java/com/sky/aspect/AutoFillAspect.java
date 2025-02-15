package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现公共字段自动填充处理逻辑
 */
@Aspect//切面注解
@Component//交给IOC容器管理
@Slf4j
public class AutoFillAspect {
    //切入点（哪些类的哪些方法进行拦截）
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)") //切入点注解+切入点表达式
    public void autoFillPointCut(){}

    /**
     * 前置通知,在通知中对公共字段进行赋值
     */
    @Before("autoFillPointCut()")
    public void AutoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段的填充");

        //获取到当前被拦截到的方法上的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature(); //方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象
        OperationType value = autoFill.value();//数据库操作类型

        //获取当前被拦截方法的参数 -- 实体对象
        Object[] args = joinPoint.getArgs(); //获得所有的参数
        if (args==null || args.length == 0) { //防止空指针异常
            return;
        }
        //有参数的话，获取第一个参数
        Object arg = args[0];

        //准备赋值的数据 -- 时间 登录id
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //根据当前不同操作类型，为对应的属性来赋值 -- 反射
        if (value == OperationType.INSERT) { //如果是插入操作
            //为4个公共字段赋值 -- 可以使用常量类里定好的字段赋值
            try {
                Method setCreateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值 -- 将准备赋值的数据赋值给公共字段
                setCreateTime.invoke(arg,now);
                setCreateUser.invoke(arg,currentId);
                setUpdateTime.invoke(arg,now);
                setUpdateUser.invoke(arg,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (value == OperationType.UPDATE){
            //为2个字段赋值
            try {
                Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setUpdateTime.invoke(arg,now);
                setUpdateUser.invoke(arg,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
