package com.dmitriev.external.stub.config.flow

import com.dmitriev.external.stub.handler.MqResponseHandler
import com.dmitriev.external.stub.service.SearchMqResponseService
import jakarta.jms.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.channel.ExecutorChannel
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.jms.dsl.Jms
import org.springframework.messaging.Message
import java.util.concurrent.Executors

@Configuration
class ProcessingConfig(
    private val connectionFactory: ConnectionFactory,
    private val searchMqResponseService: SearchMqResponseService,
) {
    @Bean
    fun mqStubFlow(): IntegrationFlow {
        return IntegrationFlow { flow -> flow
                .channel(stubChannel())
                .handle(MqResponseHandler(searchMqResponseService))
                .delay { spec -> spec
                        .messageGroupId("stubDelayer")
                        .delayExpression("headers['stubDelay'] ?: 0")
                }
                .log<Message<*>> { "Send stub response to: ${it.headers["replyTo"] ?: "INT.STUB.ERR"}" }
                .handle(Jms
                    .outboundAdapter(connectionFactory)
                    .destinationExpression("headers['replyTo'] ?: 'INT.STUB.ERR'"))
        }
    }

    @Bean
    fun stubChannel() = ExecutorChannel(executor())

    @Bean
    fun executor() = Executors.newFixedThreadPool(5)
}