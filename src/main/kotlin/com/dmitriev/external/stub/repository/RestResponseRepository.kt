package com.dmitriev.external.stub.repository

import com.dmitriev.external.stub.entity.RestResponse
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.RequestMethod
import java.time.ZonedDateTime

@Repository
interface RestResponseRepository: JpaRepository<RestResponse, Long> {
    @Query(
        """
        select distinct r.headerKey from RestResponse r
        where r.method = :method
        and r.path = :path
        and r.headerKey is not null
    """
    )
    fun findHeaderKeys(method: RequestMethod, path: String): List<String>

    @Query(
        """
        from RestResponse r where
        r.headerKey = :headerKey
        and r.headerValue = :headerValue
        and r.method = :method
        and r.path = :path
    """
    )
    fun findResponses(headerKey: String, headerValue: String, method: RequestMethod, path: String): List<RestResponse>

    @Query(
        """
        from RestResponse r where
        r.headerKey is null
        and r.headerValue is null
        and r.method = :method
        and r.path = :path
    """
    )
    fun findDefaultResponses(method: RequestMethod, path: String): List<RestResponse>

    @Modifying
    @Query("delete from RestResponse r where r.deleteAfter <=: date")
    fun deleteOutdated(date: ZonedDateTime): Int
}