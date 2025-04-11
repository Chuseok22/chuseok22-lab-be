package com.chuseok22.lab.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  private static final String BASE_URL = "https://github.com";

  @Bean
  public WebClient webClient(WebClient.Builder builder) {
    return builder
        .baseUrl(BASE_URL)
        .defaultHeaders(httpHeaders -> {
          httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        })
        .build();
  }

}
