package com.chuseok22.lab.domain.github.token.repository;

import com.chuseok22.lab.domain.github.token.domain.GithubToken;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GithubTokenRepository extends JpaRepository<GithubToken, UUID> {

  Optional<GithubToken> findByMemberMemberId(UUID memberId);
}
