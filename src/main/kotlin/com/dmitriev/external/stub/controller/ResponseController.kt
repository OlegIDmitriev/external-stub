package com.dmitriev.external.stub.controller

import com.dmitriev.external.stub.dto.MqResponseDto
import com.dmitriev.external.stub.dto.RestResponseDto
import com.dmitriev.external.stub.service.ManageMqResponseService
import com.dmitriev.external.stub.service.ManageRestResponseService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("response")
class ResponseController(
    private val manageMqResponseService: ManageMqResponseService,
    private val manageRestResponseService: ManageRestResponseService,
) {
    @PostMapping("mq")
    fun addMqResponse(
        @RequestBody mqResponseDto: MqResponseDto
    ) {
        manageMqResponseService.save(mqResponseDto.toEntity())
    }

    @PostMapping("mq/reload")
    fun reloadMqResponsesFromDb() {
        manageMqResponseService.reloadFromDb()
    }

    @PostMapping("rest")
    fun addRestResponse(
        @RequestBody restResponseDto: RestResponseDto
    ) {
        manageRestResponseService.save(restResponseDto.toEntity())
    }
}