package com.chuseok22.lab.global.util;

import com.chuseok22.lab.global.config.HttpService;
import com.chuseok22.lab.global.exception.CustomException;
import com.chuseok22.lab.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebClientService implements HttpService {

  private final WebClient webClient;

  @Override
  public String getHtml(String url) {
    return webClient.get()
        .uri(url)
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }

  @Override
  public <T> T getApiResponse(String url, Class<T> responseType) {
    log.debug("WebClient API 요청: URL={}", url);
    try {
      T response = webClient.get()
          .uri(url)
          .retrieve()
          .bodyToMono(responseType)
          .doOnSuccess(t -> log.debug("API 응답 성공: type={}", responseType.getSimpleName()))
          .doOnError(throwable -> log.error("API 요청 실패: URL={}, error={}", url, throwable.getMessage()))
          .block();
      if (response == null) {
        log.error("API 응답이 null 입니다. URL: {}", url);
        throw new CustomException(ErrorCode.RESPONSE_BODY_EMPTY);
      }
      return response;
    } catch (Exception e) {
      log.error("API 요청 오류: URL={}, error={}", url, e.getMessage());
      throw e;
    }
  }
}
