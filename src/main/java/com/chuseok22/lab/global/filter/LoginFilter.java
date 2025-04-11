package com.chuseok22.lab.global.filter;

import com.chuseok22.lab.domain.auth.dto.CustomUserDetails;
import com.chuseok22.lab.domain.auth.dto.LoginRequest;
import com.chuseok22.lab.global.exception.CustomException;
import com.chuseok22.lab.global.exception.ErrorCode;
import com.chuseok22.lab.global.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private final JwtUtil jwtUtil;
  private final AuthenticationManager authenticationManager;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final RedisTemplate<String, Object> redisTemplate;

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
    try {
      // 요청 본문에서 JSON 데이터를 읽어옴
      LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

      String username = String.valueOf(loginRequest.getUsername());
      String password = String.valueOf(loginRequest.getPassword());

      // 스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

      // token 검증을 위한 AuthenticationManager로 전달
      return authenticationManager.authenticate(authToken);
    } catch (IOException e) {
      log.error("JSON 파싱 중 오류 발생");
      throw new CustomException(ErrorCode.INVALID_REQUEST);
    }
  }

  //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
    // UserDetails
    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    // AccessToken 발급
    String accessToken = jwtUtil.createAccessToken(customUserDetails);

    // RefreshToken 발급
    String refreshToken = jwtUtil.createRefreshToken(customUserDetails);

    log.debug("로그인 성공: 엑세스 토큰 및 리프레시 토큰 생성");
    log.debug("accessToken = {}", accessToken);
    log.debug("refreshToken = {}", refreshToken);

    // RefreshToken을 Redisd에 저장 (key: RT:memberId)
    redisTemplate.opsForValue().set(
        JwtUtil.REFRESH_KEY_PREFIX + customUserDetails.getMemberId(),
        refreshToken,
        jwtUtil.getRefreshExpirationTime(),
        TimeUnit.MILLISECONDS
    );

    // 헤더에 AccessToken 추가
    response.addHeader("Authorization", "Bearer " + accessToken);
    response.setHeader("Access-Control-Expose-Headers", "Authorization");

    // 쿠키에 refreshToken 추가
    Cookie cookie = new Cookie("refreshToken", refreshToken);
    cookie.setHttpOnly(true); // HttpOnly 설정
    cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setMaxAge((int) (jwtUtil.getRefreshExpirationTime() / 1000)); // 쿠키 maxAge는 초 단위 이므로, 밀리초를 1000으로 나눔
    response.addCookie(cookie);

    // TODO: Swagger 테스트를 위한 임시 반환
//    response.getWriter().write("AccessToken: "+ accessToken + "\n");
//    response.getWriter().write("RefreshToken: " + refreshToken);
  }

  //로그인 실패시 실행하는 메소드
  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {

    log.error("로그인 실패: {}", failed.getMessage());
    response.sendError(401);
  }
}
