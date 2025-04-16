package com.chuseok22.lab.domain.github.issue.dto;

import lombok.Builder;

@Builder
public record IssueResponse(String branchName, String commitMessage) {}
