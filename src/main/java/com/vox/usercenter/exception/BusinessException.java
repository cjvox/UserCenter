package com.vox.usercenter.exception;

import com.vox.usercenter.common.ErrorCode;

/**
 * @author VOX
 * 自定义的全局异常类
 */

public class BusinessException extends RuntimeException {
    private final String description;
    private final int code;

    public BusinessException(String message, String description, int code) {
        super(message);
        this.description = description;
        this.code = code;
    }
    public BusinessException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.code=errorCode.getCode();
        this.description= errorCode.getDescription();;
    }
    public BusinessException(ErrorCode errorCode,String description){
        super(errorCode.getMessage());
        this.code=errorCode.getCode();
        this.description= description;
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }
}
