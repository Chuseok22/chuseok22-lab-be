package com.chuseok22.lab.domain.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenCategory {
  ACCESS_TOKEN("accessToken"),
  REFRESH_TOKEN("refreshToken");

  private final String prefix;
}
