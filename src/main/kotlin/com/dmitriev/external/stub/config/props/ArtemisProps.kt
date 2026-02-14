package com.dmitriev.external.stub.config.props

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("artemis")
data class ArtemisProps(
    val url: String,
    val user: String?,
    val password: String?,
)
