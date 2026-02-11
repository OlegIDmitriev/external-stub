package com.dmitriev.external.stub.service

import com.dmitriev.external.stub.entity.MqResponse
import com.dmitriev.external.stub.repository.MqResponseRepository
import io.micrometer.observation.ObservationRegistry
import jakarta.annotation.PostConstruct
import jakarta.jms.ConnectionFactory
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.context.IntegrationFlowContext
import org.springframework.integration.jms.dsl.Jms
import org.springframework.stereotype.Service
import org.springframework.util.backoff.ExponentialBackOff

@Service
class ManageMqResponseService(
    private val flowContext: IntegrationFlowContext,
    private val connectionFactory: ConnectionFactory,
    private val mqResponseRepository: MqResponseRepository,
    private val observationRegistry: ObservationRegistry,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @EventListener(ApplicationReadyEvent::class)
    fun init() {
        val savedQueues = mqResponseRepository.findAllQueues()
        savedQueues.forEach {
            flowContext.registration(createReadFlow(it))
                .id(it)
                .register()
        }
    }

    fun save(mqResponse: MqResponse) {
        val isQueueAlreadySaved = mqResponseRepository.existsByQueue(mqResponse.queue)
        if (!isQueueAlreadySaved) {
            flowContext.registration(createReadFlow(mqResponse.queue))
                .id(mqResponse.queue)
                .register()
        }
    }

    fun reloadFromDb() {
        log.info("Reload read mq flows from db")
        destroyAllReadFlows()
        init()
        log.info("Reload read mq flows from db")
    }

    fun createReadFlow(queue: String): IntegrationFlow {
        log.info("Register stub flow for queue: $queue")
        return IntegrationFlow.from(
            Jms.messageDrivenChannelAdapter(
                Jms
                    .container(connectionFactory, queue)
                    .backOff(ExponentialBackOff())
                    .observationRegistry(observationRegistry)
            )
        )
            .enrichHeaders{ e -> e.header("stubQueue", queue, true)}
            .channel("stubChannel")
            .get()
    }

    private fun destroyAllReadFlows() {
        flowContext.registry.forEach {
            log.info("Destroy IntegrationFlow ${it.value.id}")
            it.value.destroy()
        }
    }
}