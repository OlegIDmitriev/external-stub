package com.dmitriev.external.stub.handler

import com.dmitriev.external.stub.service.SearchMqResponseService
import org.slf4j.LoggerFactory
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder

class MqResponseHandler(
    private val mqResponseService: SearchMqResponseService
) : AbstractReplyProducingMessageHandler() {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun handleRequestMessage(requestMessage: Message<*>): Any? {
        val queue = requestMessage.headers["stubQueue"] as String
        val requestPayload = requestMessage.payload as String

        log.info("$queue. Message with headers ${requestMessage.headers}")
        val response = mqResponseService.search(queue, requestPayload, requestMessage.headers)

        if (response != null) {
            return MessageBuilder
                .withPayload(response.responsePayload)
                .copyHeaders(requestMessage.headers)
                .setHeader("stubDelay", response.delayInSec)
                .build()
        }

        return null
    }
}