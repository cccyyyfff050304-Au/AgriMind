package com.agrimind.common.exception;

import com.agrimind.common.result.Result;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
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

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class,
            ConstraintViolationException.class
    })
    public Result<Void> handleValidationException(Exception exception) {
        return Result.fail(400, exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception exception) {
        log.error("Unhandled server exception", exception);
        return Result.fail(500, "internal server error");
    }
}
