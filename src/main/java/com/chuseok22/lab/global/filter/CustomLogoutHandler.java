package com.chuseok22.lab.global.filter;

import static com.chuseok22.lab.domain.auth.vo.TokenCategory.ACCESS_TOKEN;
import static com.chuseok22.lab.domain.auth.vo.TokenCategory.REFRESH_TOKEN;

import com.chuseok22.lab.domain.auth.dto.CustomUserDetails;
import com.chuseok22.lab.global.util.CookieUtil;
import com.chuseok22.lab.global.util.JwtUtil;
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
  private final CookieUtil cookieUtil;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

    // 사용자 정보 획득
    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
    log.debug("CustomUserDetails: {}", customUserDetails.getMemberId());

    // Redis에서 리프레시 토큰 삭제
    String key = JwtUtil.REFRESH_KEY_PREFIX + customUserDetails.getMemberId();
    jwtUtil.deleteRefreshToken(key);

    // 기존 쿠키 삭제
    response.addCookie(cookieUtil.createDeleteCookie(ACCESS_TOKEN.getPrefix()));
    response.addCookie(cookieUtil.createDeleteCookie(REFRESH_TOKEN.getPrefix()));
    response.setStatus(HttpServletResponse.SC_OK);
  }
}

