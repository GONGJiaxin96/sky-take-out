package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /***
     * 处理SQL异常
     * @param sqlIntegrityConstraintViolationException
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException sqlIntegrityConstraintViolationException){
        //获得异常信息
        String message = sqlIntegrityConstraintViolationException.getMessage();
        //异常信息里是否包含 Duplicate entry
        if (message.contains("Duplicate entry")) {
            //返回提示信息用户名已经存在 将信息动态提交，通过空格分隔得到数组对象，名字在第三格
            String[] split = message.split(" ");
            String username = split[2];
            String msg = username + MessageConstant.ALREADY_EXISTS; //这里不要使用字符串了，使用常量类里定义的内容
            return Result.error(msg);
        }else {
            return Result.error(MessageConstant.UNKNOWN_ERROR); //发生未知错误,提示信息全在常量类里面
        }
    }

}
