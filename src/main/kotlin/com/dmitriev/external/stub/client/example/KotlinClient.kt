package com.dmitriev.external.stub.client.example

import com.dmitriev.external.stub.dto.MqResponseDto
import com.dmitriev.external.stub.dto.RestResponseDto
import com.dmitriev.external.stub.dto.enums.PayloadType
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity

class KotlinClient(
    private val url: String,
    private val restTemplate: RestTemplate = RestTemplate()
) {
    fun stubMq(
        queue: String,
        headerKey: String? = null,
        headerValue: String? = null,
        responseBody: String,
        matchingExpression: String? = null,
    ) {
        val dto = MqResponseDto(
            queue = queue,
            headerKey = headerKey,
            headerValue = headerValue,
            responsePayload = responseBody,
            requestType = PayloadType.JSON,
            requestMatchExpression = matchingExpression,
            delayInSec = 0L,
            ttlInSec = 0L,
        )

        restTemplate.postForEntity<String>("$url/response/mq", dto)
    }

    fun stubRest(
        method: RequestMethod,
        path: String,
        headerKey: String? = null,
        headerValue: String? = null,
        responseStatus: Int,
        responseBody: String,
    ) {
        val dto = RestResponseDto(
            method = method,
            requestPath = path,
            headerKey = headerKey,
            headerValue = headerValue,
            responseStatus = responseStatus,
            responseBody = responseBody,
            delayInSec = 0L,
            ttlInSec = 0L,
        )

        restTemplate.postForEntity<String>("$url/response/mq", dto)
    }
}