package com.dmitriev.external.stub.config

import com.dmitriev.external.stub.config.props.ArtemisProps
import io.micrometer.observation.ObservationRegistry
import jakarta.jms.ConnectionFactory
import jakarta.jms.DeliveryMode
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.core.JmsTemplate

@EnableJms
@Configuration
class MqConfig(
    private val artemisProps: ArtemisProps,
) {
    @Bean
    fun connectionFactory(): ConnectionFactory {
        return ActiveMQConnectionFactory(artemisProps.url, artemisProps.user, artemisProps.password)
    }

    @Bean
    fun jmsTemplate(
        observationRegistry: ObservationRegistry,
        connectionFactory: ConnectionFactory,
    ): JmsTemplate {
        val jmsTemplate = JmsTemplate()
        jmsTemplate.connectionFactory = connectionFactory
        jmsTemplate.setObservationRegistry(observationRegistry)
        jmsTemplate.deliveryMode = DeliveryMode.PERSISTENT
        return jmsTemplate
    }
}