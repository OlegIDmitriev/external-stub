package com.dmitriev.external.stub.service

import com.dmitriev.external.stub.dto.enums.PayloadType
import com.dmitriev.external.stub.entity.MqResponse
import com.dmitriev.external.stub.repository.MqResponseRepository
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import org.slf4j.LoggerFactory
import org.springframework.messaging.MessageHeaders
import org.springframework.stereotype.Service
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

@Service
class SearchMqResponseService(
    private val mqResponseRepository: MqResponseRepository,
) {
    private val log = LoggerFactory.getLogger(this::class.java)


    fun search(queue: String, requestBody: String, headers: MessageHeaders): MqResponse? {
        val headerKeys = mqResponseRepository.findHeaderKeys(queue)
        if (headerKeys.isEmpty()) {
            log.info("Correlation headers not set for queue $queue")
            return searchDefault(queue, requestBody)
        }

        log.info("Found next correlation headers: $headerKeys for $queue")
        val correlationHeaderPairs = headerKeys
            .map { it to headers[it] }
            .filter { it.second != null }

        log.info("Request message contains next potential headers: $correlationHeaderPairs")
        val mqResponses = correlationHeaderPairs
            .map{
                mqResponseRepository.findResponses(
                    headerKey = it.first,
                    headerValue = it.second.toString(),
                    queue = queue
                )
            }
            .firstOrNull {it.isNotEmpty()}

        if (!mqResponses.isNullOrEmpty()) {
            val correlationKey = mqResponses[0].headerKey
            val correlationValue = mqResponses[0].headerValue
            log.info("Found ${mqResponses.size} response for $correlationKey=$correlationValue")

            val matchingResponse = mqResponses.firstOrNull{ matches(it, requestBody) }
            if (matchingResponse != null) {
                val matchingExp = matchingResponse.matchingExpression
                log.info("$queue, $correlationKey=$correlationValue matches=$matchingExp")
                return matchingResponse
            } else {
                val matchExps = mqResponses.map { it.matchingExpression }.joinToString(",")
                log.warn("Matching response not found. Total stub responses: ${mqResponses.size} with match exp: $matchExps")
            }
        }

        log.warn("Request message headers not match any saved response")
        return searchDefault(queue, requestBody)
    }

    private fun searchDefault(queue: String, requestBody: String): MqResponse? {
        log.info("Search default response for queue $queue")
        val defaultResponses = mqResponseRepository.findDefaultResponses(queue)

        if (defaultResponses.isEmpty()) {
            log.warn("Not found any default response for queue $queue")
            return null
        }

        val response = defaultResponses.firstOrNull {matches(it, requestBody)}
        if (response != null) {
            val matchingExp = response.matchingExpression
            log.info("$queue, default answer matches=$matchingExp")
        } else {
            val matchExps = defaultResponses.map { it.matchingExpression }.joinToString(",")
            log.warn("Matching default response not found. Total stub responses: ${defaultResponses.size} with match exp: $matchExps")
        }
        return response
    }

    private fun matches(mqResponse: MqResponse, requestPayload: String): Boolean {
        if (mqResponse.matchingExpression == null) {
            return true
        }

        return when (mqResponse.payloadType) {
            PayloadType.JSON -> jsonMatches(requestPayload, mqResponse.matchingExpression)
            PayloadType.XML -> xmlMatches(requestPayload, mqResponse.matchingExpression)
        }
    }

    private fun xmlMatches(xml: String, xmlPath: String): Boolean {
        try {
            val builderFactory = DocumentBuilderFactory.newInstance()
            val builder = builderFactory.newDocumentBuilder()
            val xmlDocument = builder.parse(InputSource(xml))
            val xPath = XPathFactory.newInstance().newXPath()
            val nodeList = xPath.compile(xmlPath).evaluate(xmlDocument, XPathConstants.NODESET) as NodeList
            return nodeList.length > 0
        } catch (ex: Exception) {
            log.warn("XPath matching error: ", ex)
            return false
        }
    }

    private fun jsonMatches(json: String, jsonPath: String): Boolean {
        val configuration = Configuration.builder()
            .options(Option.ALWAYS_RETURN_LIST, Option.SUPPRESS_EXCEPTIONS)
            .build()

        val context = JsonPath.parse(json, configuration)
        val results = context.read<List<*>>(jsonPath)
        return results.isNotEmpty()
    }
}