package com.sparta.springresttemplateclient.naver.service;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "naver.client")
public class ApiKey {
    private String naver_client_id;
    private String naver_client_secret;

    // Getter 메서드
    public String getId() {
        return naver_client_id;
    }

    public String getSecret() {
        return naver_client_secret;
    }

    @Override
    public String toString() {
        return "ApiKey [apiKey=" + naver_client_id + "]";
    }
}