package com.dmitriev.external.stub.controller

import com.dmitriev.external.stub.service.SearchRestResponseService
import io.swagger.v3.oas.annotations.Hidden
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.*
import org.springframework.web.bind.annotation.RestController

@Hidden
@RestController
@RequestMapping("stub")
class StubController(
    private val searchRestResponseService: SearchRestResponseService
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @RequestMapping(path = ["**"], method = [GET, PUT, POST, PATCH, DELETE])
    fun mockRest(request: HttpServletRequest): ResponseEntity<String> {
        log.info("request: ${request.method} ${request.requestURI}")
        val response = searchRestResponseService.search(request)
        log.info("response: ${response.statusCode} ${response.body}")

        return response
    }
}