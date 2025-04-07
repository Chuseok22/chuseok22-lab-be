package com.chuseok22.lab.global.filter;

import com.chuseok22.lab.domain.member.domain.Member;
import com.chuseok22.lab.domain.member.repository.MemberRepository;
import com.chuseok22.lab.global.exception.CustomException;
import com.chuseok22.lab.global.exception.ErrorCode;
import com.chuseok22.lab.global.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

  private final JwtUtil jwtUtil;
  private final MemberRepository memberRepository;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

    // 1. 쿠키에서 리프레시 토큰 추출 및 삭제
    String refreshToken = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("refreshToken".equals(cookie.getName())) {
          refreshToken = cookie.getValue();
          // 쿠키 삭제
          cookie.setMaxAge(0);
          cookie.setPath("/");
          response.addCookie(cookie);
          log.debug("리프레시 토큰 쿠키 삭제 완료");
          break;
        }
      }
    }

    // 2. Redis에서 리프레시 토큰 삭제
    if (authentication != null && refreshToken != null) {
      String username = jwtUtil.getUsername(refreshToken);
      Member member = memberRepository.findByUsername(username)
          .orElseThrow(() -> {
            log.error("리프레시 토큰에 등록된 사용자를 찾을 수 없습니다.");
            return new CustomException(ErrorCode.MEMBER_NOT_FOUND);
          });

      // 리프레시 토큰 삭제
      String key = JwtUtil.REFRESH_KEY_PREFIX + member.getMemberId();
      jwtUtil.deleteRefreshToken(key);
    }
  }
}

