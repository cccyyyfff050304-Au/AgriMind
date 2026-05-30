package com.agrimind.common.exception;

import com.agrimind.common.result.Result;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException exception) {
        return Result.fail(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldError();
        String message = fieldError == null ? "参数校验失败" : fieldError.getDefaultMessage();
        return Result.fail(400, message);
    }

    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldError();
        String message = fieldError == null ? "参数校验失败" : fieldError.getDefaultMessage();
        return Result.fail(400, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException exception) {
        return Result.fail(400, exception.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public Result<Void> handleDataAccessException(DataAccessException exception) {
        log.error("Database access exception", exception);
        Throwable mostSpecificCause = exception.getMostSpecificCause();
        String message = mostSpecificCause == null ? exception.getMessage() : mostSpecificCause.getMessage();
        return Result.fail(500, "数据库访问失败: " + message);
    }

    @ExceptionHandler(CannotCreateTransactionException.class)
    public Result<Void> handleCannotCreateTransactionException(CannotCreateTransactionException exception) {
        log.error("Database transaction exception", exception);
        Throwable mostSpecificCause = exception.getMostSpecificCause();
        String message = mostSpecificCause == null ? exception.getMessage() : mostSpecificCause.getMessage();
        return Result.fail(500, "数据库连接失败: " + message);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception exception) {
        log.error("Unhandled server exception", exception);
        return Result.fail(500, "internal server error");
    }
}
