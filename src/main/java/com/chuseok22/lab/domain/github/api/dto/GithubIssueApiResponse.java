package com.chuseok22.lab.domain.github.api.dto;

import lombok.Builder;

@Builder
public record GithubIssueApiResponse(String title, String issueUrl) {

}
