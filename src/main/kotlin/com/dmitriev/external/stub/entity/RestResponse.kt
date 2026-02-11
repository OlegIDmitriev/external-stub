package com.dmitriev.external.stub.entity

import jakarta.persistence.*
import org.springframework.web.bind.annotation.RequestMethod
import java.io.Serializable
import java.time.ZonedDateTime

@Entity
class RestResponse(
    val headerKey: String?,
    val headerValue: String?,
    @Enumerated(EnumType.STRING)
    val method: RequestMethod,
    val requestPath: String,
    val responseStatus: Int,
    val responseBody: String?,
    val delayInSec: Long?,
    val deleteAfter: ZonedDateTime?,

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "restResponseSeq")
    @SequenceGenerator(name = "restResponseSeq", sequenceName = "resr_response_seq", allocationSize = 1)
    var id: Long? = null,
) : Serializable
