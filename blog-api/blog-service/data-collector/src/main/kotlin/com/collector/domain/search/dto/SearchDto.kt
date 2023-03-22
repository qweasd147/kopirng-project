package com.collector.domain.search.dto

import com.blog.r2dbc.entity.SearchLog
import java.time.LocalDateTime

class SaveSearchLogRequest(
    private val contents: String,
    private val createdAt: LocalDateTime,
) {

    fun toSearchLog(): SearchLog {

        return SearchLog(
            contents = this.contents,
        ).also {
            it.updateCreatedAt(this.createdAt)
        }
    }
}

class SearchWordResponseDto(
    var word: String,
    var count: Long,
)