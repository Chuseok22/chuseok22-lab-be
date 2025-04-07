package com.chuseok22.lab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableMongoAuditing
@EnableAsync
public class LabApplication {

  public static void main(String[] args) {
    SpringApplication.run(LabApplication.class, args);
  }

}
