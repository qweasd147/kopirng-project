package com.blog.controller

import com.blog.dto.SearchRequest
import com.collector.domain.search.dto.SearchWordResponseDto
import com.collector.domain.search.service.SearchWordQueryService
import com.external.blog.BlogApiClient
import com.external.blog.dto.BlogResponseDto
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.*

@RestController
@RequestMapping("/v1")
class BlogApiController(
    private val blogApiClient: BlogApiClient,
    private val searchWordQueryService: SearchWordQueryService,
) {

    @GetMapping("/search")
    suspend fun searchBlog(@Valid param: SearchRequest): BlogResponseDto {

        return this.blogApiClient.searchBlog(param)
    }

    @GetMapping("/word")
    suspend fun getTop10word(): List<SearchWordResponseDto> {

        return this.searchWordQueryService.findTopWord(10)
    }
}