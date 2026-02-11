package com.dmitriev.external.stub.client.example;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

public class JavaClient {
    private final String url;
    private final RestTemplate restTemplate;

    public JavaClient(String url) {
        this.url = url;
        this.restTemplate = new RestTemplate();
    }

    public void stubMq(String queue, String headerKey, String headerValue, String responseBody, String matchingExpression) {
        var dto = new MqResponseDto(queue, headerKey, headerValue, responseBody, matchingExpression);
        restTemplate.postForEntity(url + "/response/mq", dto, String.class);
    }

    public void stubRest(RequestMethod method, String path, String headerKey, String headerValue, Integer responseStatus, String responseBody) {
        var dto = new RestResponseDto(method, path, headerKey, headerValue, responseStatus, responseBody);
        restTemplate.postForEntity(url + "/response/rest", dto, String.class);
    }

    record MqResponseDto(
            String queue,
            String headerKey,
            String headerValue,
            String responseBody,
            String matchingExpression
    ) {}

    record RestResponseDto(
            RequestMethod method,
            String path,
            String headerKey,
            String headerValue,
            Integer responseStatus,
            String responseBody
    ) {}
}
