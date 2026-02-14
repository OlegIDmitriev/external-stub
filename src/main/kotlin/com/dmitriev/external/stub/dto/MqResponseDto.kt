package com.dmitriev.external.stub.dto

import com.dmitriev.external.stub.dto.enums.PayloadType
import com.dmitriev.external.stub.entity.MqResponse
import org.apache.logging.log4j.util.Strings.trimToNull
import java.time.ZonedDateTime

data class MqResponseDto(
    val headerKey: String?,
    val headerValue: String?,
    val queue: String,
    val responsePayload: String,
    val requestType: PayloadType?,
    val requestMatchExpression: String?,
    val delayInSec: Long?,
    val ttlInSec: Long?,
) {
    fun toEntity(): MqResponse {
        val now = ZonedDateTime.now();
        val deleteAfter = when {
            ttlInSec == null -> null
            (ttlInSec > 0) -> now.plusSeconds(ttlInSec)
            else -> null
        }

        return MqResponse(
            headerKey = trimToNull(headerKey),
            headerValue = trimToNull(headerValue),
            queue = trimToNull(queue),
            responseBody = trimToNull(responsePayload),
            payloadType = requestType ?: PayloadType.JSON,
            matchingExpression = trimToNull(requestMatchExpression),
            delayInSec = delayInSec ?: 0L,
            deleteAfter = deleteAfter,
        )
    }
}
