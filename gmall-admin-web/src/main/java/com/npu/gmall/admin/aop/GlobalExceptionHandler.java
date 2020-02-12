package com.npu.gmall.admin.aop;

import com.npu.gmall.to.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一处理所有异常，给前端返回500的json
 * 当我们编写环绕通知的时候，目标方法出现的异常一定要再次抛出去
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value={ArithmeticException.class})
    public Object handlerException(Exception e){
        log.error("系统出现异常，信息：{}",e.getMessage());
        return new CommonResult().validateFailed("数学运算异常...");
    }

    @ExceptionHandler(value={NullPointerException.class})
    public Object handlerException01(Exception e){
        log.error("系统出现异常，信息：{}",e.getMessage());
        return new CommonResult().validateFailed("空指针了...");
    }
}
