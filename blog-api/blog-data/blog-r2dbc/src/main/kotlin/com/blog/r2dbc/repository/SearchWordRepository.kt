package com.blog.r2dbc.repository

import com.blog.r2dbc.entity.SearchWord
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SearchWordRepository: CoroutineCrudRepository<SearchWord, Long> {

    suspend fun findAllByWordIn(words: List<String>): List<SearchWord>

    @Query("SELECT * FROM search_word ORDER BY count DESC LIMIT :limit")
    suspend fun findAllTop(limit: Int): List<SearchWord>
}