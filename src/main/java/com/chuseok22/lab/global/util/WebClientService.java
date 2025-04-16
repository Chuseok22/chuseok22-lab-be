package com.chuseok22.lab.global.util;

import static com.chuseok22.lab.global.util.CommonUtil.*;

import com.chuseok22.lab.global.exception.CustomException;
import com.chuseok22.lab.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebClientService implements WebService {

  private final WebClient webClient;

  @Override
  public JsonNode getJson(String url) {
    return getJson(url, null);
  }

  @Override
  public JsonNode getJson(String url, String token) {
    log.debug("WebClient API 요청: URL={}", url);

    WebClient.RequestHeadersSpec<?> request = webClient.get().uri(url);
    if (!nvl(token, "").isEmpty()) {
      request = request.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }

    JsonNode response = request
          .retrieve()
          .bodyToMono(JsonNode.class)
          .doOnSuccess(json -> log.debug("API 응답 성공"))
          .doOnError(throwable -> log.error("API 요청 실패: URL={}, error={}", url, throwable.getMessage()))
          .block();

    if (response == null) {
      log.error("API 응답 Body가 없습니다.");
      throw new CustomException(ErrorCode.INVALID_RESPONSE_BODY);
    }
    return response;
  }
}
