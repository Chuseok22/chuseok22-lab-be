package com.chuseok22.lab.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  @Value("${encryption.key}")
  private String encryptionKey;

  @Value("${encryption.salt}")
  private String encryptionSalt;

  @Bean
  public WebClient webClient() {
    return WebClient.builder()
        .defaultHeaders(httpHeaders -> {
          httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        })
        .codecs(clientCodecConfigurer -> clientCodecConfigurer
            .defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // 16MB
        .build();
  }

  @Bean
  public TextEncryptor textEncryptor() {
    return Encryptors.text(encryptionKey, encryptionSalt);
  }
}
