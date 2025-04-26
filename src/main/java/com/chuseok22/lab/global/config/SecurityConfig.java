package com.chuseok22.lab.global.config;

import com.chuseok22.lab.global.filter.CustomLogoutHandler;
import com.chuseok22.lab.global.filter.LoginFilter;
import com.chuseok22.lab.global.filter.TokenAuthenticationFilter;
import com.chuseok22.lab.global.util.CookieUtil;
import com.chuseok22.lab.global.util.JwtUtil;
import java.util.Arrays;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtUtil jwtUtil;
  private final CookieUtil cookieUtil;
  private final AuthenticationConfiguration authenticationConfiguration;
  private final CustomLogoutHandler customLogoutHandler;

  /**
   * Security Filter Chain 설정
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    LoginFilter loginFilter = new LoginFilter(jwtUtil, authenticationManager(authenticationConfiguration), cookieUtil);
    loginFilter.setFilterProcessesUrl("/api/auth/login");

    return http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests((authorize) -> authorize
            .requestMatchers(SecurityUrls.AUTH_WHITELIST.toArray(new String[0]))
            .permitAll() // AUTH_WHITELIST에 등록된 URL은 인증 허용
            .requestMatchers(SecurityUrls.ADMIN_PATHS.toArray(new String[0]))
            .hasRole("ADMIN") // ADMIN_PATHS에 등록된 URL은 관리자만 접근가능
            .anyRequest().authenticated()
        )
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout") // "/logout" 경로로 접근 시 로그아웃
            .addLogoutHandler(customLogoutHandler)
            .logoutSuccessUrl("/") // 로그아웃 성공 후 root 경로로 이동
            .invalidateHttpSession(true)
        )
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .addFilterBefore(
            new TokenAuthenticationFilter(jwtUtil, cookieUtil),
            UsernamePasswordAuthenticationFilter.class
        )
        .addFilterAt(
            loginFilter, UsernamePasswordAuthenticationFilter.class
        )
        .build();
  }

  /**
   * 인증 메니저 설정
   */
  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration)
      throws Exception {

    return authenticationConfiguration.getAuthenticationManager();
  }

  /**
   * CORS 설정 소스 빈
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(SecurityUrls.ALLOWED_ORIGINS);
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setAllowCredentials(true);
    configuration.setAllowedHeaders(Collections.singletonList("*"));
    configuration.setExposedHeaders(Arrays.asList("Authorization"));
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
    urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", configuration);
    return urlBasedCorsConfigurationSource;
  }

  /**
   * 비밀번호 인코더 빈 (BCrypt)
   */
  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
