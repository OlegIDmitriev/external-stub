package com.dmitriev.external.stub

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = ["com.dmitriev.external.stub.config.props"])
class ExternalStubApplication

fun main(args: Array<String>) {
	runApplication<ExternalStubApplication>(*args)
}
