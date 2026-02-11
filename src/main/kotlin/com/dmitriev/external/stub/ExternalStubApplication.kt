package com.dmitriev.external.stub

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ExternalStubApplication

fun main(args: Array<String>) {
	runApplication<ExternalStubApplication>(*args)
}
