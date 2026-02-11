package com.dmitriev.external.stub.config

import com.dmitriev.external.stub.repository.MqResponseRepository
import com.dmitriev.external.stub.repository.RestResponseRepository
import com.dmitriev.external.stub.service.ManageMqResponseService
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.ZonedDateTime

@Component
@EnableScheduling
class SchedulerConfig(
    private val mqResponseRepository: MqResponseRepository,
    private val restResponseRepository: RestResponseRepository,
    private val manageMqResponseService: ManageMqResponseService,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    fun deleteOutdatedStubResponses() {
        log.info("Deleting outdated responses")

        val now = ZonedDateTime.now()
        val mqResponsesCount = mqResponseRepository.deleteOutdated(now)
        log.info("Deleted $mqResponsesCount mq responses")
        val restResponsesCount = restResponseRepository.deleteOutdated(now)
        log.info("Deleted $restResponsesCount rest responses")
        manageMqResponseService.reloadFromDb()

        log.info("Deleting finished")
    }
}