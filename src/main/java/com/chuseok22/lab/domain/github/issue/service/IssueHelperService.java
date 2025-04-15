package com.chuseok22.lab.domain.github.issue.service;

import com.chuseok22.lab.domain.github.issue.domain.IssueHelper;
import com.chuseok22.lab.domain.github.issue.dto.IssueRequest;
import com.chuseok22.lab.domain.github.issue.dto.IssueResponse;
import com.chuseok22.lab.domain.github.issue.repository.IssueHelperRepository;
import com.chuseok22.lab.global.exception.CustomException;
import com.chuseok22.lab.global.exception.ErrorCode;
import com.chuseok22.lab.global.util.WebClientService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class IssueHelperService {

  private final WebClientService webClientService;
  private final IssueHelperRepository issueHelperRepository;

  /**
   * 입력된 URL을 처리해 브랜치명과 커밋 케시지 반환
   * 1. DB에서 URL조회
   * 2. 있으면 DB 데이터 반환
   * 3. 없으면 파싱 후 저장 및 반환
   */
  public IssueResponse processIssueHelper(IssueRequest request) {

    String issueUrl = request.getIssueUrl();
    try {
      IssueHelper issueHelper = issueHelperRepository.findByIssueUrl(issueUrl);
      if (issueHelper == null) {
        log.debug("새로운 URL, 파싱 시작: {}", issueUrl);
        issueHelper = parseIssue(issueUrl);
      } else {
        log.debug("DB에서 기존 이슈 조회: {}", issueUrl);
      }
      return IssueResponse.builder()
          .branchName(issueHelper.getBranchName())
          .commitMessage(issueHelper.getCommitMessage())
          .build();
    } catch (Exception e) {
      log.error("Issue 처리중 오류 발생: URL={}, error={}", issueUrl, e.getMessage());
      throw new CustomException(ErrorCode.GITHUB_ISSUE_PROCESSING_ERROR);
    }
  }


  /**
   * 사용자로부터 URL을 입력 받아 브랜치 명, 커밋 메시지를 생성 후 저장
   *
   * @param issueUrl issueUrl
   * @return branchName, commitMessage
   */
  public IssueHelper parseIssue(String issueUrl) {
    try {
      // WebClient로 HTML 가져오기
      String html = webClientService.getHtml(issueUrl);

      // HTML 파싱
      Document document = Jsoup.parse(html);
      String rawTitle = document.title();
      log.debug("파싱된 원본 제목: {}", rawTitle);

      // 이슈 번호 추출
      String issueNumber = extractIssueNumber(issueUrl);

      // 이슈 제목 추출
      String issueTitle = extractIssueTitle(rawTitle);

      // 브랜치명 생성
      String branchName = createBranchName(issueTitle, issueNumber);

      // 커밋 메시지 생성
      String commitMessage = createCommitMessage(issueTitle, issueUrl);

      // IssueHelper 객체 생성
      IssueHelper issueHelper = IssueHelper.builder()
          .issueUrl(issueUrl)
          .branchName(branchName)
          .commitMessage(commitMessage)
          .build();

      log.debug("Issue 파싱 성공: {}", issueHelper.getIssueUrl());
      return issueHelperRepository.save(issueHelper);
    } catch (Exception e) {
      log.error("Issue 파싱중 오류 발생: {}", e.getMessage());
      throw new CustomException(ErrorCode.GITHUB_ISSUE_PARSING_ERROR);
    }
  }

  // 이슈 번호 추출
  private String extractIssueNumber(String issueUrl) {
    String issueNumber = issueUrl.substring(issueUrl.lastIndexOf("/") + 1);
    log.debug("이슈 번호: {}", issueNumber);
    return issueNumber;
  }

  // 이슈 제목 추출
  private String extractIssueTitle(String rawTitle) {
    // 1. '· Issue #' 이후의 부분 제거
    int issueIndex = rawTitle.indexOf("· Issue #");
    String title = issueIndex != -1 ? rawTitle.substring(0, issueIndex).trim() : rawTitle.trim();

    // 2. 태그([기능개선][알림]) 제거
    String cleanedTitle = title.replaceAll("\\[.*?]", "").trim();
    if (cleanedTitle.equals(title) && !title.startsWith("[")) {
      // 태그가 없는 경우 원본 제목 사용
      cleanedTitle = title;
    }

    // 3. 이모지(🚀) 및 불필요한 공백 제거
    cleanedTitle = cleanedTitle.replaceAll("[\\p{So}\\p{C}\\uFE0F\\u200D]", "").trim();

    log.debug("가공된 이슈 제목: {}", cleanedTitle);
    return cleanedTitle;
  }

  // 브랜치명 생성
  private String createBranchName(String issueTitle, String issueNumber) {
    String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    String formattedTitle = issueTitle.replaceAll("[^a-zA-Z0-9가-힣]", "_");
    String branchName = String.format("%s_#%s_%s", date, issueNumber, formattedTitle);
    log.debug("생성된 브랜치명: {}", branchName);
    return branchName;
  }

  // 커밋 메시지 생성
  private String createCommitMessage(String issueTitle, String issueUrl) {
    String commitMessage = String.format("%s : feat : {변경 사항에 대한 설명} %s", issueTitle, issueUrl).trim();
    log.debug("생성된 커밋 메시지: {}", commitMessage);
    return commitMessage;
  }
}
