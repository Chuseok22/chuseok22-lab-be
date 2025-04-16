package com.chuseok22.lab.domain.github.token.service;

import com.chuseok22.lab.domain.github.token.domain.GithubToken;
import com.chuseok22.lab.domain.github.token.repository.GithubTokenRepository;
import com.chuseok22.lab.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubTokenService {

  private final GithubTokenRepository githubTokenRepository;
  private final TextEncryptor textEncryptor;

  /**
   * 회원의 GithubToken을 저장 or 업데이트 합니다
   */
  public void saveOrUpdateToken(Member member, String githubToken) {
    String encryptedToken = textEncryptor.encrypt(githubToken);
    GithubToken existingToken = githubTokenRepository.findByMemberMemberId(member.getMemberId())
        .orElseGet(() -> {
              // 토큰이 없으면 요청된 토큰 저장
              log.debug("새로운 Github Token을 저장합니다. 사용자: {}", member.getUsername());
              return githubTokenRepository.save(GithubToken.builder()
                  .member(member)
                  .token(encryptedToken)
                  .build());
            }
        );
    if (!existingToken.getToken().equals(encryptedToken)) { // 기존에 저장된 토큰과 다른 경우
      existingToken.setToken(encryptedToken);
      githubTokenRepository.save(existingToken);
      log.debug("사용자: {} Github Token 업데이트", member.getUsername());
    }
  }

  /**
   * 회원의 GithubToken을 반환합니다
   */
  public String getMemberGithubToken(Member member) {
    GithubToken githubToken = githubTokenRepository
        .findByMemberMemberId(member.getMemberId()).orElse(null);

    if (githubToken != null) { // 저장된 토큰 존재
      return textEncryptor.decrypt(githubToken.getToken());
    } else { // 저장된 토큰이 없으면 null 반환
      return null;
    }
  }
}
