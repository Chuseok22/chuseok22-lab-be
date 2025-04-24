package com.chuseok22.lab.domain.member.controller;

import com.chuseok22.lab.domain.auth.dto.CustomUserDetails;
import com.chuseok22.lab.domain.member.dto.MemberInfoResponse;
import com.chuseok22.lab.domain.member.service.MemberService;
import com.chuseok22.lab.global.aspect.LogMonitoringInvocation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
@Tag(
    name = "회원 API",
    description = "회원 관련 API 제공"
)
public class MemberController implements MemberControllerDocs{

  private final MemberService memberService;

  @Override
  @GetMapping("")
  @LogMonitoringInvocation
  public ResponseEntity<MemberInfoResponse> getMemberInfo(
      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
    return ResponseEntity.ok(memberService.getMemberInfo(customUserDetails.getMember()));
  }
}
