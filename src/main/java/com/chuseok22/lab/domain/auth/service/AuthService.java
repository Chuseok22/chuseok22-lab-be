package com.chuseok22.lab.domain.auth.service;

import com.chuseok22.lab.domain.auth.dto.JoinRequest;
import com.chuseok22.lab.domain.member.domain.Member;
import com.chuseok22.lab.domain.member.repository.MemberRepository;
import com.chuseok22.lab.domain.member.vo.Role;
import com.chuseok22.lab.global.exception.CustomException;
import com.chuseok22.lab.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

  private final MemberRepository memberRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  /**
   * 회원가입 로직
   */
  @Transactional
  public void join(JoinRequest request) {

    // 1. 중복 아이디 검증
    if (!validateUsername(request.getUsername())) {
      throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
    }

    // 2. 중복 닉네임 검증
    if (!validateNickname(request.getNickname())) {
      throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
    }

    // 3. 회원가입
    memberRepository.save(Member.builder()
        .username(request.getUsername())
        .password(bCryptPasswordEncoder.encode(request.getPassword()))
        .nickname(request.getNickname())
        .role(Role.ROLE_USER)
        .build());
    log.debug("회원: {} 회원가입 성공", request.getUsername());
  }

  /**
   * 아이디 검증
   */
  @Transactional
  public Boolean validateUsername(String username) {
    if (memberRepository.existsByUsername(username)) {
      log.error("중복된 아이디입니다. 아이디: {}", username);
      return false;
    }
    return true;
  }

  /**
   * 닉네임 검증
   */
  @Transactional
  public Boolean validateNickname(String nickname) {
    if (memberRepository.existsByNickname(nickname)) {
      log.error("중복된 닉네임입니다. 닉네임: {}", nickname);
      return false;
    }
    return true;
  }
}
