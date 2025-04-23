package com.chuseok22.lab.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServerUtil {

  private final Environment environment;

  public String getActiveProfile() {
    String[] activeProfiles = environment.getActiveProfiles();
    if (activeProfiles.length == 0) {
      return "default";
    }

    for (String profile : activeProfiles) {
      if ("dev".equalsIgnoreCase(profile)) {
        return "dev";
      } else if ("prod".equalsIgnoreCase(profile)) {
        return "prod";
      }
    }
    return activeProfiles[0];
  }

  public boolean isDevProfile() {
    return "dev".equalsIgnoreCase(getActiveProfile());
  }

  public boolean isProdProfile() {
    return "prod".equalsIgnoreCase(getActiveProfile());
  }

}
