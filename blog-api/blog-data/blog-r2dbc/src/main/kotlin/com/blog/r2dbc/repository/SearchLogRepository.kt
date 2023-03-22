package com.blog.r2dbc.repository

import com.blog.r2dbc.entity.SearchLog
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface SearchLogRepository : CoroutineCrudRepository<SearchLog, Long> {

    @Query("SELECT * FROM search_log WHERE is_collected = false and idx < :maxIdx ORDER BY idx DESC LIMIT :limit")
    suspend fun findAllUnCollected(limit: Int, maxIdx: Long): List<SearchLog>

    @Query("SELECT * FROM search_log WHERE is_collected = false ORDER BY idx DESC LIMIT :limit")
    suspend fun findAllUnCollected(limit: Int): List<SearchLog>
}