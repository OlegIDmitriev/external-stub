package com.dmitriev.external.stub.dto

import com.dmitriev.external.stub.entity.RestResponse
import org.apache.logging.log4j.util.Strings.trimToNull
import org.springframework.web.bind.annotation.RequestMethod
import java.time.ZonedDateTime

data class RestResponseDto(
    val headerKey: String?,
    val headerValue: String?,
    val method: RequestMethod,
    val requestPath: String,
    val responseStatus: Int?,
    val responseBody: String?,
    val delayInSec: Long?,
    val ttlInSec: Long?,
) {
    fun toEntity(): RestResponse {
        val now = ZonedDateTime.now();
        val deleteAfter = when {
            ttlInSec == null -> null
            (ttlInSec > 0) -> now.plusSeconds(ttlInSec)
            else -> null
        }

        return RestResponse(
            headerKey = trimToNull(headerKey),
            headerValue = trimToNull(headerValue),
            method = method,
            path = trimToNull(requestPath),
            responseStatus = responseStatus ?: 200,
            responseBody = trimToNull(responseBody),
            delayInSec = delayInSec ?: 0L,
            deleteAfter = deleteAfter,
        )
    }
}
