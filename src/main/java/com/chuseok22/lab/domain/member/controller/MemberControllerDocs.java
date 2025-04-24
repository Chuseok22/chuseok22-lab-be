package com.chuseok22.lab.domain.member.controller;

import com.chuseok22.lab.domain.auth.dto.CustomUserDetails;
import com.chuseok22.lab.domain.member.dto.MemberInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;

public interface MemberControllerDocs {

  @Operation(
      summary = "사용자 정보",
      description = """
          
          인증: `필요`
          
          ### 요청 파라미터 (JSON)
          `없음`
          
          ### 사용 방법
          
          ### 유의사항
          """
  )
  ResponseEntity<MemberInfoResponse> getMemberInfo(
      CustomUserDetails customUserDetails);

}
