package com.dmitriev.external.stub.repository

import com.dmitriev.external.stub.entity.MqResponse
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime

@Repository
interface MqResponseRepository: JpaRepository<MqResponse, Long> {
    @Query("select distinct r.queue from MqResponse r")
    fun findAllQueues(): List<String>

    fun existsByQueue(queue: String): Boolean

    @Query(
        """
        from MqResponse r where
        r.headerKey = :headerKey
        and r.headerValue = :headerValue
        and r.queue = :queue
        order by r.matchingExpression, r.id
    """
    )
    fun findResponses(headerKey: String, headerValue: String, queue: String): List<MqResponse>

    @Query(
        """
        from MqResponse r where
        r.queue = :queue
        and r.headerKey is null
        order by r.matchingExpression, r.id 
    """
    )
    fun findDefaultResponses(queue: String): List<MqResponse>

    @Query("""
        select distinct r.headerKey from MqResponse r
        where r.queue = :queue
        and r.headerKey is not null
    """)
    fun findHeaderKeys(queue: String): List<String>

    @Modifying
    @Query("delete from MqResponse r where r.deleteAfter <=: date")
    fun deleteOutdated(date: ZonedDateTime): Int
}