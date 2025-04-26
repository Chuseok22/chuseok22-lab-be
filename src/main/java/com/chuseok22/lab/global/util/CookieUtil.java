package com.chuseok22.lab.global.util;

import static com.chuseok22.lab.domain.auth.vo.TokenCategory.ACCESS_TOKEN;
import static com.chuseok22.lab.domain.auth.vo.TokenCategory.REFRESH_TOKEN;

import com.chuseok22.lab.global.exception.CustomException;
import com.chuseok22.lab.global.exception.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CookieUtil {

  private final JwtUtil jwtUtil;
  private final ServerUtil serverUtil;

  @Value("${cookie.domain}")
  private String ROOT_DOMAIN;

  /**
   * 특정 쿠키를 반환합니다
   */
  @Transactional(readOnly = true)
  public Cookie getCookie(HttpServletRequest request, String name) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      log.error("쿠키가 존재하지 않습니다.");
      throw new CustomException(ErrorCode.COOKIES_NOT_FOUND);
    }

    for (Cookie cookie : cookies) {
      if (cookie.getName().equalsIgnoreCase(name)) {
        return cookie;
      }
    }
    log.error("{}에 해당하는 쿠키가 없습니다.", name);
    throw new CustomException(ErrorCode.COOKIES_NOT_FOUND);
  }

  /**
   * 새로운 쿠키를 발급합니다
   *
   * @return 발급된 쿠키를 반환합니다
   */
  @Transactional
  public Cookie createCookie(String name, String token) {
    if (name.equals(ACCESS_TOKEN.getPrefix())) {
      return createAccessTokenCookie(token);
    } else if (name.equals(REFRESH_TOKEN.getPrefix())) {
      return createRefreshTokenCookie(token);
    } else {
      log.error("잘못된 Cookie key가 요청됐습니다. 요청값: {}", name);
      throw new CustomException(ErrorCode.INVALID_REQUEST);
    }
  }

  /**
   * 기존 쿠키를 삭제합니다
   *
   * @return MaxAge=0 인 쿠키를 반환합니다
   */
  @Transactional
  public Cookie createDeleteCookie(String name) {
    Cookie cookie = new Cookie(name, null);
    cookie.setHttpOnly(true);
    cookie.setSecure(serverUtil.isProdProfile()); // dev 환경에서는 secure = false
    cookie.setPath("/");
    cookie.setMaxAge(0);
    return cookie;
  }

  /**
   * 엑세스 토큰이 들어있는 쿠키를 발급합니다
   * httpOnly = false
   */
  private Cookie createAccessTokenCookie(String accessToken) {
    log.debug("accessToken을 포함한 쿠키를 발급합니다.");
    Cookie cookie = new Cookie("accessToken", accessToken);
    cookie.setHttpOnly(false);
    cookie.setSecure(serverUtil.isProdProfile()); // dev 환경에서는 secure = false
    cookie.setPath("/");
    cookie.setDomain(ROOT_DOMAIN);
    cookie.setMaxAge((int) (jwtUtil.getAccessExpirationTime() / 1000));
    return cookie;
  }

  /**
   * 리프레시 토큰이 들어있는 쿠키를 발급합니다.
   * httpOnly = true
   */
  private Cookie createRefreshTokenCookie(String refreshToken) {
    log.debug("refreshToken을 포함한 쿠키를 발급합니다.");
    Cookie cookie = new Cookie("refreshToken", refreshToken);
    cookie.setHttpOnly(true);
    cookie.setSecure(serverUtil.isProdProfile()); // dev 환경에서는 secure = false
    cookie.setPath("/");
    cookie.setDomain(ROOT_DOMAIN);
    cookie.setMaxAge((int) (jwtUtil.getRefreshExpirationTime() / 1000));
    return cookie;
  }
}
