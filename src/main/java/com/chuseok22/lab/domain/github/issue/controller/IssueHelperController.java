package com.chuseok22.lab.domain.github.issue.controller;

import com.chuseok22.lab.domain.auth.dto.CustomUserDetails;
import com.chuseok22.lab.domain.github.issue.dto.IssueRequest;
import com.chuseok22.lab.domain.github.issue.dto.IssueResponse;
import com.chuseok22.lab.domain.github.issue.service.IssueHelperService;
import com.chuseok22.lab.domain.member.domain.Member;
import com.chuseok22.lab.global.aspect.LogMonitoringInvocation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/github/issue")
@Tag(
    name = "Github Issue API",
    description = "Github Issue 관련 API 제공"
)
@Slf4j
public class IssueHelperController implements IssueHelperControllerDocs {

  private final IssueHelperService issueHelperService;

  @Override
  @PostMapping(value = "")
  @LogMonitoringInvocation
  public ResponseEntity<IssueResponse> processIssueHelper(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      @RequestBody @Valid IssueRequest request) {
    Member member = customUserDetails.getMember();
    return ResponseEntity.ok(issueHelperService.processIssueHelper(member, request));
  }
}
