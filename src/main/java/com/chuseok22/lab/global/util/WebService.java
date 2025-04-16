package com.chuseok22.lab.global.util;

import com.fasterxml.jackson.databind.JsonNode;

public interface WebService {

  JsonNode getJson(String url);

  JsonNode getJson(String url, String token);
}
