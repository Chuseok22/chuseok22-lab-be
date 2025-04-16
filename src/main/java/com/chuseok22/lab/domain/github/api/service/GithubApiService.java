package com.chuseok22.lab.domain.github.api.service;

import com.chuseok22.lab.domain.github.api.dto.GithubIssueApiResponse;
import com.chuseok22.lab.domain.github.token.service.GithubTokenService;
import com.chuseok22.lab.domain.member.domain.Member;
import com.chuseok22.lab.global.exception.CustomException;
import com.chuseok22.lab.global.exception.ErrorCode;
import com.chuseok22.lab.global.util.WebClientService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubApiService {

  private final WebClientService webClientService;
  private final GithubTokenService githubTokenService;

  private static final String GITHUB_API_ROOT_URL = "https://api.github.com/repos/";

  public GithubIssueApiResponse fetchIssue(Member member, String issueUrl, String requestToken) {

    String apiUrl = convertToApiUrl(issueUrl);
    String token;
    if (requestToken != null) { // 토큰이 요청 된 경우
      githubTokenService.saveOrUpdateToken(member, requestToken); // 토큰 저장 및 업데이트
      token = requestToken;
    } else { // 토큰이 요청 되지 않은 경우
      token = githubTokenService.getMemberGithubToken(member);
    }

    try {
      JsonNode json = webClientService.getJson(apiUrl, token);
      String title = json.get("title").asText();
      if (title == null) {
        log.error("Github Issue 응답 JSON에 title이 존재하지 않습니다.");
        throw new CustomException(ErrorCode.INVALID_RESPONSE_BODY);
      }
      log.debug("URL: {} Github API 요청 성공", issueUrl);
      return GithubIssueApiResponse.builder()
          .title(title)
          .issueUrl(issueUrl)
          .build();
    } catch (WebClientResponseException e) {
      if (e.getStatusCode().is4xxClientError()) {
        if (token == null) {
          log.error("Private 레포지토리 접근 시 토큰이 필요합니다.");
          throw new CustomException(ErrorCode.GITHUB_TOKEN_REQUIRED);
        }
        log.error("잘못된 깃허브 토큰입니다.");
        throw new CustomException(ErrorCode.INVALID_GITHUB_TOKEN);
      }
      log.error("API 요청 시 오류가 발생했습니다. 에러: {}", e.getMessage());
      throw new CustomException(ErrorCode.GITHUB_API_ERROR);
    }
  }

  // API요청을 위한 URL 변환
  private String convertToApiUrl(String issueUrl) {
    // https://github.com/owner/repo/issues/123 -> https://api.github.com/repos/owner/repo/issues/123
    String path = issueUrl.replace("https://github.com/", "");
    return GITHUB_API_ROOT_URL + path;
  }
}
