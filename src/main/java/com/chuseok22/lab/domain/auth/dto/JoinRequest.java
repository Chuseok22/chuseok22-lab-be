package com.chuseok22.lab.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class JoinRequest {

  @NotBlank(message = "이이디를 입력하세요")
  @Schema(defaultValue = "exampleUsername123")
  private String username;
  @NotBlank(message = "비밀번호를 입력하세요")
  @Schema(defaultValue = "examplePassword123")
  private String password;
  @NotBlank(message = "닉네임을 입력하세요")
  @Schema(defaultValue = "exampleNickname123")
  private String nickname;
}
