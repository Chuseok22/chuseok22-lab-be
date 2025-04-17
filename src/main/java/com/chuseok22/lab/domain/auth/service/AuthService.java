package com.chuseok22.lab.domain.auth.service;

import com.chuseok22.lab.domain.auth.dto.CustomUserDetails;
import com.chuseok22.lab.domain.auth.dto.JoinRequest;
import com.chuseok22.lab.domain.member.domain.Member;
import com.chuseok22.lab.domain.member.repository.MemberRepository;
import com.chuseok22.lab.domain.member.vo.Role;
import com.chuseok22.lab.global.exception.CustomException;
import com.chuseok22.lab.global.exception.ErrorCode;
import com.chuseok22.lab.global.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

  private final MemberRepository memberRepository;
  private final JwtUtil jwtUtil;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  /**
   * 회원가입 로직
   */
  @Transactional
  public void join(JoinRequest request) {

    // 1. 중복 아이디 검증
    if (!validateUsername(request.getUsername())) {
      throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
    }

    // 2. 중복 닉네임 검증
    if (!validateNickname(request.getNickname())) {
      throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
    }

    // 3. 회원가입
    memberRepository.save(Member.builder()
        .username(request.getUsername())
        .password(bCryptPasswordEncoder.encode(request.getPassword()))
        .nickname(request.getNickname())
        .role(Role.ROLE_USER)
        .build());
    log.debug("회원: {} 회원가입 성공", request.getUsername());
  }

  /**
   * 쿠키에 저장된 refreshToken을 통해 accessToken, refreshToken을 재발급합니다.
   */
  @Transactional
  public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
    log.debug("accessToken이 만료되어 재발급을 진행합니다.");
    String refreshToken = null;

    // 쿠키에서 리프레시 토큰 추출
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      log.error("쿠키가 존재하지 않습니다.");
      throw new CustomException(ErrorCode.COOKIES_NOT_FOUND);
    }
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals("refreshToken")) {
        refreshToken = cookie.getValue();
        break;
      }
    }
    // 리프레시 토큰이 없는 경우
    if (refreshToken == null || refreshToken.isBlank()) {
      log.error("쿠키에서 refreshToken을 찾을 수 없습니다.");
      throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }

    // 해당 refreshToken이 유효한지 검증
    isValidateRefreshToken(refreshToken);

    // 새로운 accessToken, refreshToken 발급
    CustomUserDetails customUserDetails = (CustomUserDetails) jwtUtil
        .getAuthentication(refreshToken).getPrincipal();
    String newAccessToken = jwtUtil.createAccessToken(customUserDetails);
    String newRefreshToken = jwtUtil.createRefreshToken(customUserDetails);

    // 기존 refreshToken 삭제
    String key = JwtUtil.REFRESH_KEY_PREFIX + customUserDetails.getMemberId();
    jwtUtil.deleteRefreshToken(key);

    // 새로운 refreshToken 저장
    jwtUtil.saveRefreshToken(key, newRefreshToken);

    // 헤더에 AccessToken 추가
    response.addHeader("Authorization", "Bearer " + newAccessToken);

    // 쿠키에 refreshToken 추가
    Cookie cookie = new Cookie("refreshToken", newRefreshToken);
    cookie.setHttpOnly(true); // HttpOnly 설정
    cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setMaxAge((int) (jwtUtil.getRefreshExpirationTime() / 1000)); // 쿠키 maxAge는 초 단위 이므로, 밀리초를 1000으로 나눔
    response.addCookie(cookie);

    // TODO: Swagger 테스트를 위한 임시 반환
    try {
      response.getWriter().write("AccessToken: " + newAccessToken + "\n");
      response.getWriter().write("RefreshToken: " + newRefreshToken);
    } catch (IOException e) {
      log.error("응답값 생성 중 오류 발생: {}", e.getMessage());
      throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * 아이디 검증
   */
  @Transactional
  public Boolean validateUsername(String username) {
    if (memberRepository.existsByUsername(username)) {
      log.error("중복된 아이디입니다. 아이디: {}", username);
      return false;
    }
    return true;
  }

  /**
   * 닉네임 검증
   */
  @Transactional
  public Boolean validateNickname(String nickname) {
    if (memberRepository.existsByNickname(nickname)) {
      log.error("중복된 닉네임입니다. 닉네임: {}", nickname);
      return false;
    }
    return true;
  }

  /**
   * 요청된 리프레시 토큰이 유효한지 검증합니다.
   */
  private void isValidateRefreshToken(String token) {
    if (jwtUtil.isExpired(token)) { // 리프레시 토큰 만료 여부 확인
      log.error("refreshToken이 만료되었습니다.");
      throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
    }

    // 토큰이 refresh인지 확인 (발급 시 페이로드에 명시)
    String category = jwtUtil.getCategory(token);
    if (!category.equals("refresh")) {
      log.error("요청된 토큰이 refreshToken이 아닙니다. 요청된 토큰 카테고리: {}", category);
      throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
    }
  }
}
