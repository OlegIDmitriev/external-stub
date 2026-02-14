package com.dmitriev.external.stub.entity

import com.dmitriev.external.stub.dto.enums.PayloadType
import jakarta.persistence.*
import java.io.Serializable
import java.time.ZonedDateTime

@Entity
class MqResponse(
    val headerKey: String?,
    val headerValue: String?,
    val queue: String,
    val responseBody: String,
    @Enumerated(EnumType.STRING)
    val payloadType: PayloadType,
    val matchingExpression: String?,
    val delayInSec: Long,
    val deleteAfter: ZonedDateTime?,

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mqResponseSeq")
    @SequenceGenerator(name = "mqResponseSeq", sequenceName = "mq_response_seq", allocationSize = 1)
    var id: Long? = null,
) : Serializable
