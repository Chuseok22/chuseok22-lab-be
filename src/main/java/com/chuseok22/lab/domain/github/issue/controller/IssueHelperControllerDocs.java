package com.chuseok22.lab.domain.github.issue.controller;

import com.chuseok22.lab.domain.github.issue.dto.IssueRequest;
import com.chuseok22.lab.domain.github.issue.dto.IssueResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;

public interface IssueHelperControllerDocs {
  @Operation(
      summary = "Issue Helper",
      description = """
          
          인증: `필요`
          
          ### 요청 파라미터 (JSON)
          - **issueUrl** (String): Issue URL [필수]
          
          ### 사용 방법
          - Github Issue URL을 입력하면 브랜치명과 커밋 메시지 명을 반환합니다.
          
          ### 유의사항
          """
  )
  ResponseEntity<IssueResponse> processIssueHelper(IssueRequest request);

}
