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
  public WebClient webClient() {
    return WebClient.builder()
        .baseUrl(BASE_URL)
        .defaultHeaders(httpHeaders -> {
          httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        })
        .codecs(clientCodecConfigurer -> clientCodecConfigurer
            .defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // 16MB
        .build();
  }
}
