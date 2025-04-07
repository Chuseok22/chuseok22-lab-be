package com.chuseok22.lab.domain.auth.service;

import com.chuseok22.lab.domain.auth.dto.CustomUserDetails;
import com.chuseok22.lab.domain.member.domain.Member;
import com.chuseok22.lab.domain.member.repository.MemberRepository;
import com.chuseok22.lab.global.exception.CustomException;
import com.chuseok22.lab.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    Member savedMember = memberRepository.findByUsername(username)
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    return new CustomUserDetails(savedMember);
  }
}
