package cn.shaines.datainterface.controller;

import cn.shaines.datainterface.model.Result;
import cn.shaines.datainterface.util.ResultHandle;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description: 全局异常处理
 * @author: houyu
 * @create: 2019-01-12 23:17
 */
@ControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        e.printStackTrace();
        return ResultHandle.getFailResult(e.getMessage());
    }
}
