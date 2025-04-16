package com.chuseok22.lab.global.exception.controller;

import com.chuseok22.lab.global.exception.CustomException;
import com.chuseok22.lab.global.exception.ErrorCode;
import com.chuseok22.lab.global.exception.ErrorResponse;
import com.chuseok22.lab.global.exception.ValidErrorResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * 1) 커스텀 예외 처리
   */
  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
    log.error("CustomException 발생: {}", e.getMessage(), e);

    ErrorCode errorCode = e.getErrorCode();
    ErrorResponse response = ErrorResponse.builder()
        .errorCode(errorCode)
        .errorMessage(e.getMessage())
        .build();

    return ResponseEntity.status(errorCode.getStatus()).body(response);
  }

  /**
   * 2) Validation 예외 처리
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
    log.error("ValidationException 발생: {}", e.getMessage(), e);
    // Validation 에러 정보를 담을 Map 생성
    Map<String, String> validation = new HashMap<>();
    for (FieldError fieldError : e.getFieldErrors()) {
      validation.put(fieldError.getField(), fieldError.getDefaultMessage());
    }

    // 공통 응답 DTO를 활용해 반환
    // ErrorCode.INVALID_REQUEST -> 400
    ValidErrorResponse response = ValidErrorResponse.builder()
        .errorCode(HttpStatus.BAD_REQUEST.toString())
        .errorMessage("잘못된 요청입니다.")
        .validation(validation)
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * 3) 그 외 예외 처리
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleException(Exception e) {
    log.error("Unhandled Exception 발생: {}", e.getMessage(), e);

    // 예상치 못한 예외 => 500
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
  }
}
