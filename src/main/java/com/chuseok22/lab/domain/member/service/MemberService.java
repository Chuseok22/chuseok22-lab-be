package com.chuseok22.lab.domain.member.service;

import com.chuseok22.lab.domain.member.domain.Member;
import com.chuseok22.lab.domain.member.dto.MemberInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

  /**
   * 사용자 정보 반환
   */
  @Transactional(readOnly = true)
  public MemberInfoResponse getMemberInfo(Member member) {
    return MemberInfoResponse.builder()
        .username(member.getUsername())
        .build();
  }
}
