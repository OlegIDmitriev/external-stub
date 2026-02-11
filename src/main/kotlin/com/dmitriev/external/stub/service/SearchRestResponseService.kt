package com.dmitriev.external.stub.service

import com.dmitriev.external.stub.repository.RestResponseRepository
import jakarta.servlet.http.HttpServletRequest
import org.apache.commons.lang3.StringUtils.strip
import org.apache.commons.lang3.StringUtils.substringAfter
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestMethod

@Service
class SearchRestResponseService(
    private val restResponseRepository: RestResponseRepository,
) {
    private val log = LoggerFactory.getLogger(this::class.java)


    fun search(request: HttpServletRequest): ResponseEntity<String> {
        val method = RequestMethod.resolve(request.method)!!
        val path = normalizePath(request.requestURI)

        val headerKeys = restResponseRepository.findHeaderKeys(method, path).filter { it.isNotBlank() }
        log.info("Found next correlation headers: $headerKeys for $method $path")
        if (headerKeys.isEmpty()) {
            return searchDefaultResponse(method, path)
        }

        val correlationHeaderPairs = headerKeys
            .map { it to request.getHeader(it) }
            .filter { it.second != null }


        log.info("Request contains next potential correlation headers: $correlationHeaderPairs")
        val restResponses = correlationHeaderPairs
            .map {
                restResponseRepository.findResponses(
                    headerKey = it.first,
                    headerValue = it.second.toString(),
                    method = method,
                    path = path,
                )
            }
            .firstOrNull{ it.isNotEmpty()}

        if (!restResponses.isNullOrEmpty()) {
            val response = restResponses[0]
            return ResponseEntity
                .status(response.responseStatus)
                .body(response.responseBody)
        }

        log.warn("Request message headers not match any saved response")
        return searchDefaultResponse(method, path)
    }

    private fun searchDefaultResponse(method: RequestMethod, path: String): ResponseEntity<String> {
        log.info("Search default responses for $method $path")

        val defaultResponses = restResponseRepository.findDefaultResponses(
            method = method,
            path = path,
        )
        if (defaultResponses.isEmpty()) {
            log.warn("Not found any default response for $method $path")
            return ResponseEntity
                .status(504)
                .body("Stub response not found!")
        }

        val response = defaultResponses[0]
        return ResponseEntity
            .status(response.responseStatus)
            .body(response.responseBody)
    }

    private fun normalizePath(path: String): String {
        return substringAfter(strip(path, "/"), "stub/")
    }
}