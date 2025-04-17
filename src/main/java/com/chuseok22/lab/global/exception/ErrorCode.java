package com.chuseok22.lab.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  // GLOBAL

  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 발생했습니다."),

  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

  ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근이 거부되었습니다."),

  // AUTH

  INVALID_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 엑세스 토큰입니다."),

  INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 리프레시 토큰입니다."),

  MISSING_AUTH_TOKEN(HttpStatus.NOT_FOUND, "토큰이 존재하지 않습니다."),

  EXPIRED_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "엑세스 토큰이 만료되었습니다."),

  EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다."),

  DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "이미 사용중인 아이디입니다."),

  DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "이미 사용중인 닉네임입니다."),

  COOKIES_NOT_FOUND(HttpStatus.NOT_FOUND, "쿠키가 존재하지 않습니다."),

  REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "리프레시 토큰을 찾을 수 없습니다."),

  // MEMBER

  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),

  // GITHUB

  GITHUB_ISSUE_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Github 이슈 처리 과정 중 오류가 발생했습니다."),

  GITHUB_ISSUE_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Github 이슈 파싱 중 오류가 발생했습니다."),

  GITHUB_ISSUE_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Github 이슈 저장 중 오류가 발생했습니다."),

  GITHUB_ISSUE_INVALID_TITLE(HttpStatus.BAD_REQUEST, "Github 이슈 제목이 잘못되었습니다"),

  GITHUB_TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, "Private 레포지토리에 접근하기위해 Token이 필요합니다."),

  INVALID_GITHUB_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 Github Token이 요청되었습니다."),

  GITHUB_API_ERROR(HttpStatus.BAD_REQUEST, "Github API 요청에 실패했습니다."),

  // WEB API

  INVALID_RESPONSE_BODY(HttpStatus.BAD_REQUEST, "잘못된 응답 Body 입니다."),
  ;

  private final HttpStatus status;
  private final String message;
}
