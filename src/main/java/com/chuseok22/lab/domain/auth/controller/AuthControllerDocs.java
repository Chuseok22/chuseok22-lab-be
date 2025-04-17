package com.chuseok22.lab.domain.auth.controller;

import com.chuseok22.lab.domain.auth.dto.JoinRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthControllerDocs {

  @Operation(
      summary = "회원가입",
      description = """
          
          인증: `불필요`
          
          ### 요청 파라미터 (JSON)
          - **username** (String): 아이디 [필수]
          - **password** (String): 비밀번호 [필수]
          - **nickname** (String): 닉네임 [필수]
          
          ### 사용 방법
          
          ### 유의사항
          - 아이디, 닉네임 중복 불가능
          """
  )
  ResponseEntity<Void> join(JoinRequest request);

  @Operation(
      summary = "엑세스 토큰 재발급",
      description = """
          
          인증: `불필요`
          
          ### 요청 파라미터 (Parameter)
          `없읍`
          
          ### 사용 방법
          - httpOnly=true 설정되어있는 쿠키에서 리프레시 토큰을 꺼내 엑세스 토큰을 재발급합니다.
          
          ### 유의사항
          """
  )
  ResponseEntity<Void> refreshAccessToken(HttpServletRequest request, HttpServletResponse response);

  @Operation(
      summary = "아이디 중복 검증",
      description = """
          
          인증: `불필요`
          
          ### 요청 파라미터 (Parameter)
          - **username** (String): 아이디 [필수]
          
          ### 사용 방법
          
          ### 유의사항
          - true: 사용가능 아이디 / false: 중복된 아이디
          """
  )
  ResponseEntity<Boolean> isValidationUsername(String username);

  @Operation(
      summary = "닉네임 중복 검증",
      description = """
          
          인증: `불필요`
          
          ### 요청 파라미터 (Parameter)
          - **nickname** (String): 닉네임 [필수]
          
          ### 사용 방법
          
          ### 유의사항
          - true: 사용가능 닉네임 / false: 중복된 닉네임
          """
  )
  ResponseEntity<Boolean> isValidationNickname(String nickname);
}
