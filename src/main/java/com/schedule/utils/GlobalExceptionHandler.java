package com.schedule.utils;

import com.schedule.common.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({InvalidFormatException.class, MismatchedInputException.class, MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<BaseResponse> handleJsonParseException(Exception ex) {
        // 返回一个友好的错误信息
        BaseResponse response = new BaseResponse(HttpStatus.BAD_REQUEST.value(), "Invalid request body", null, null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleGeneralException(Exception ex) {
        // 处理其他类型的异常
        BaseResponse response = new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", null, null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
