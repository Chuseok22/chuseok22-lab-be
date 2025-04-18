package com.chuseok22.lab.global.config;

import java.util.Arrays;
import java.util.List;

/**
 * Security 관련 URL 상수 관리
 */
public class SecurityUrls {

  /**
   * 허용된 CORS Origin 목록
   */
  public static final List<String> ALLOWED_ORIGINS = Arrays.asList(
      "https://api.lab.chuseok22.com", // 메인 API 서버
      "https://api-test.lab.chuseok22.com", // 테스트 API 서버


      "https://lab.chuseok22.com", // 메인 클라이언트
      "https://test.lab.chuseok22.com", // 테스트 클라이언트

      "http://localhost:8080", // 로컬 API 서버
      "http://localhost:3000" // 로컬 웹 서버
  );

  /**
   * 인증을 생략할 URL 패턴 목록
   */
  public static final List<String> AUTH_WHITELIST = Arrays.asList(
      // API
      "/api/auth/join", // 회원가입
      "/api/auth/login", // 로그인
      "/api/auth/refresh", // 액세스 토큰 재발급
      "/api/auth/validate/username", // 아이디 중복 검증
      "/api/auth/validate/nickname", // 닉네임 중복 검증
      "/", // 홈


      // Swagger
      "/docs/**", // Swagger UI
      "/v3/api-docs/**" // Swagger API 문서

  );

  /**
   * 관리자 권한이 필요한 URL 패턴 목록
   */
  public static final List<String> ADMIN_PATHS = Arrays.asList(


  );

}