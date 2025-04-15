package com.chuseok22.lab.global.config;

public interface HttpService {

  String getHtml(String url);

  <T> T getApiResponse(String url, Class<T> responseType);
}
