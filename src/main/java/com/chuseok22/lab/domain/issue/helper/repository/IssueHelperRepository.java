package com.chuseok22.lab.domain.issue.helper.repository;

import com.chuseok22.lab.domain.issue.helper.domain.IssueHelper;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueHelperRepository extends JpaRepository<IssueHelper, UUID> {

  Optional<IssueHelper> findByIssueUrl(String issueUrl);
}
