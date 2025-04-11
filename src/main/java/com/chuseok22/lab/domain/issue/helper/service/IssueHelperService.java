package com.chuseok22.lab.domain.issue.helper.service;

import com.chuseok22.lab.domain.issue.helper.domain.IssueHelper;
import com.chuseok22.lab.domain.issue.helper.dto.IssueRequest;
import com.chuseok22.lab.domain.issue.helper.dto.IssueResponse;
import com.chuseok22.lab.domain.issue.helper.repository.IssueHelperRepository;
import com.chuseok22.lab.global.exception.CustomException;
import com.chuseok22.lab.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class IssueHelperService {

  private final WebClient webClient;
  private final IssueHelperRepository issueHelperRepository;

  /**
   * 입력된 URL을 처리해 브랜치명과 커밋 케시지 반환
   * 1. DB에서 URL조회
   * 2. 있으면 DB 데이터 반환
   * 3. 없으면 파싱 후 저장 및 반환
   */
  public Mono<IssueResponse> processIssueHelper(IssueRequest request) {
    // DB 조회 (블로킹 JPA 호출을 Mono로 래핑)
    return Mono.fromCallable(() -> issueHelperRepository.findByIssueUrl(request.getIssueUrl()))
        .subscribeOn(Schedulers.boundedElastic()) // 블로킹 작업을 별도 스레드에서 실행
        .flatMap(optIssueHelper -> {
          if (optIssueHelper.isPresent()) {
            log.debug("DB에서 기존 이슈 조회: {}", request.getIssueUrl());
            IssueHelper issueHelper = optIssueHelper.get();
            return Mono.just(IssueResponse.builder()
                .branchName(issueHelper.getBranchName())
                .commitMessage(issueHelper.getCommitMessage())
                .build());
          }
          log.debug("새로운 URL, 파싱 시작: {}", request.getIssueUrl());
          return parseIssue(request)
              .map(issueHelper -> IssueResponse.builder()
                  .branchName(issueHelper.getBranchName())
                  .commitMessage(issueHelper.getCommitMessage())
                  .build());
        })
        .onErrorResume(throwable -> {
          log.error("이슈 처리 실패: {}", throwable.getMessage());
          return Mono.error(new CustomException(ErrorCode.GITHUB_ISSUE_PROCESSING_ERROR));
        });
  }


  /**
   * 사용자로부터 URL을 입력 받아 브랜치 명, 커밋 메시지를 생성 후 저장
   *
   * @param request issueUrl
   * @return branchName, commitMessage
   */
  public Mono<IssueHelper> parseIssue(IssueRequest request) {
    return webClient.get()
        .uri(request.getIssueUrl())
        .retrieve()
        .bodyToMono(String.class)
        .map(html -> {
          // HTML 파싱
          Document document = Jsoup.parse(html);
          String rawTitle = document.title();
          log.debug("파싱된 원본 제목: {}", rawTitle);

          // 이슈 번호 추출
          String issueNumber = extractIssueNumber(request.getIssueUrl());
          log.debug("이슈 번호: {}", issueNumber);

          // 이슈 제목 추출
          String issueTitle = extractIssueTitle(rawTitle);
          log.debug("가공된 이슈 제목: {}", issueTitle);

          // 브랜치명 생성
          String branchName = createBranchName(issueTitle, issueNumber);
          log.debug("생성된 브랜치명: {}", branchName);

          // 커밋 메시지 생성
          String commitMessage = createCommitMessage(issueTitle, request.getIssueUrl());
          log.debug("생성된 커밋 메시지: {}", commitMessage);

          // IssueHelper 객체 생성
          return IssueHelper.builder()
              .issueUrl(request.getIssueUrl())
              .branchName(branchName)
              .commitMessage(commitMessage)
              .build();
        })
        .flatMap(issueHelper ->
            // DB 저장 (블로킹 JPA 호출을 Mono로 래핑)
            Mono.<IssueHelper>fromCallable(() -> issueHelperRepository.save(issueHelper))
                .subscribeOn(Schedulers.boundedElastic())
                .map(savedIssue -> {
                  log.debug("DB에 이슈 저장 완료: {}", savedIssue.getIssueUrl());
                  return savedIssue;
                })
        )
        .onErrorResume(throwable -> {
          log.error("이슈 DB 저장 실패: {}", throwable.getMessage());
          return Mono.error(new CustomException(ErrorCode.GITHUB_ISSUE_SAVE_ERROR));
        });
  }

  // 이슈 번호 추출
  private String extractIssueNumber(String issueUrl) {
    return issueUrl.substring(issueUrl.lastIndexOf("/") + 1);
  }

  // 이슈 제목 추출
  private String extractIssueTitle(String rawTitle) {
    // 1. '· Issue #' 이후의 부분 제거
    int issueIndex = rawTitle.indexOf("· Issue #");
    String title = issueIndex != -1 ? rawTitle.substring(0, issueIndex).trim() : rawTitle.trim();

    // 2. 태그([기능개선][알림]) 제거
    String cleanedTitle = title.replaceAll(".*?\\](.*)", "$1").trim();
    if (cleanedTitle.equals(title) && !title.startsWith("[")) {
      // 태그가 없는 경우 원본 제목 사용
      cleanedTitle = title;
    }

    // 3. 이모지(🚀) 및 불필요한 공백 제거
    cleanedTitle = cleanedTitle.replaceAll("[\\p{So}\\p{Cntrl}]", "").trim();

    return cleanedTitle;
  }

  // 브랜치명 생성
  private String createBranchName(String issueTitle, String issueNumber) {
    String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    String formattedTitle = issueTitle.replaceAll("[^a-zA-Z0-9가-힣]", "_");
    return String.format("%s_#%s_%s", date, issueNumber, formattedTitle);
  }

  // 커밋 메시지 생성
  private String createCommitMessage(String issueTitle, String issueUrl) {
    return String.format("%s : feat : {변경 사항에 대한 설명} %s", issueTitle, issueUrl);
  }
}
