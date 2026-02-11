package com.dmitriev.external.stub.config

import io.micrometer.observation.ObservationRegistry
import jakarta.jms.ConnectionFactory
import jakarta.jms.DeliveryMode
import org.apache.activemq.ActiveMQConnectionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.core.JmsTemplate

@EnableJms
@Configuration
class MqConfig {
    @Bean
    fun connectionFactory(
        @Value("\${activemq.user}") user: String,
        @Value("\${activemq.password}") password: String,
        @Value("\${activemq.broker-url}") brokerUrl: String,
    ): ConnectionFactory {
        val connectionFactory = ActiveMQConnectionFactory(user, password, brokerUrl)
        connectionFactory.trustedPackages = listOf("com.dmitriev.mq.stub");
        return connectionFactory;
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