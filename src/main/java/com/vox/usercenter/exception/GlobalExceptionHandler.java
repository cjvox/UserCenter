package com.vox.usercenter.exception;

import com.vox.usercenter.common.BaseResponse;
import com.vox.usercenter.common.ErrorCode;
import com.vox.usercenter.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author VOX
 * 全局异常处理
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException exception){
        log.error("BusinessException"+exception.getMessage()+"/t"+exception.getDescription());
        return ResultUtils.error(exception.getCode(), exception.getMessage(), exception.getDescription());
    }
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException exception){
        log.error("runTimeException",exception);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, exception.getMessage(),"");
    }
}
