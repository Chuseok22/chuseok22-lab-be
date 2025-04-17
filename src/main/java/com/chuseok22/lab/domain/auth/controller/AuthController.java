package com.chuseok22.lab.domain.auth.controller;

import com.chuseok22.lab.domain.auth.dto.JoinRequest;
import com.chuseok22.lab.domain.auth.dto.LoginRequest;
import com.chuseok22.lab.domain.auth.service.AuthService;
import com.chuseok22.lab.global.aspect.LogMonitoringInvocation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(
    name = "인증 API",
    description = "인증 관련 API 제공"
)
public class AuthController implements AuthControllerDocs{

  private final AuthService authService;

  // TODO: Swagger 테스트를 위한 임시 메서드
  @PostMapping("/login")
  @LogMonitoringInvocation
  public ResponseEntity<Void> login(@RequestBody LoginRequest request) {
    return ResponseEntity.ok().build();
  }

  @Override
  @PostMapping("/join")
  @LogMonitoringInvocation
  public ResponseEntity<Void> join(@RequestBody @Valid JoinRequest request) {
    authService.join(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Override
  @PostMapping("/refresh")
  @LogMonitoringInvocation
  public ResponseEntity<Void> refreshAccessToken(
      HttpServletRequest request, HttpServletResponse response) {
    authService.refreshAccessToken(request, response);
    return ResponseEntity.ok().build();
  }

  @Override
  @GetMapping("/validate/username")
  @LogMonitoringInvocation
  public ResponseEntity<Boolean> isValidationUsername(@RequestParam String username) {
    return ResponseEntity.ok(authService.validateUsername(username));
  }

  @Override
  @GetMapping("/validate/nickname")
  @LogMonitoringInvocation
  public ResponseEntity<Boolean> isValidationNickname(@RequestParam String nickname) {
    return ResponseEntity.ok(authService.validateNickname(nickname));
  }
}
