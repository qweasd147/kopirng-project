package com.collector.domain.search.service

import com.blog.r2dbc.entity.SearchLog
import com.blog.r2dbc.repository.SearchLogRepository
import com.collector.domain.search.dto.SaveSearchLogRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
internal class SearchLogService(
    private val searchLogRepository: SearchLogRepository,
) {

    @Transactional
    suspend fun saveLog(saveSearchLogRequest: SaveSearchLogRequest) {

        this.searchLogRepository.save(saveSearchLogRequest.toSearchLog())
    }

    suspend fun findAllByUnCollected(limit: Int, maxIdx: Long? = null): List<SearchLog> {

        return if (maxIdx == null) {
            searchLogRepository.findAllUnCollected(limit)
        } else {
            searchLogRepository.findAllUnCollected(limit, maxIdx)
        }
    }
}