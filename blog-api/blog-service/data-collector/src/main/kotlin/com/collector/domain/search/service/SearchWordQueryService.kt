package com.collector.domain.search.service

import com.blog.r2dbc.repository.SearchWordRepository
import com.collector.domain.search.dto.SearchWordResponseDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SearchWordQueryService(
    private val searchWordRepository: SearchWordRepository
) {

    suspend fun findTopWord(limit: Int): List<SearchWordResponseDto> {

        return this.searchWordRepository.findAllTop(limit)
            .map {
                SearchWordResponseDto(
                    word = it.word,
                    count = it.count
                )
            }
    }
}