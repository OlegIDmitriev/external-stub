package com.dmitriev.external.stub.service

import com.dmitriev.external.stub.entity.RestResponse
import com.dmitriev.external.stub.repository.RestResponseRepository
import org.slf4j.LoggerFactory

class ManageRestResponseService(
    private val restResponseRepository: RestResponseRepository,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun save(restResponse: RestResponse) {
        restResponseRepository.save(restResponse)
    }
}