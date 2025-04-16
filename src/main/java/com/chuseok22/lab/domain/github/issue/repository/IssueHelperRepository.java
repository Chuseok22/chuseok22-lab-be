package com.chuseok22.lab.domain.github.issue.repository;

import com.chuseok22.lab.domain.github.issue.domain.IssueHelper;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueHelperRepository extends JpaRepository<IssueHelper, UUID> {

  IssueHelper findByIssueUrl(String issueUrl);
}
