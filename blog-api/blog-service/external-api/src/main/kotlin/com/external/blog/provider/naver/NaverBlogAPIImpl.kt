package com.external.blog.provider.naver

import com.external.blog.provider.BlogAPIProvider
import com.external.blog.dto.BlogResponseDto
import com.external.blog.dto.BlogSearchRequestDto
import com.external.blog.provider.ProviderType
import com.external.blog.provider.naver.dto.NaverBlogRequestDto
import mu.KotlinLogging
import org.springframework.stereotype.Component

//네이버는 circuit breaker 사용 x
//const val NAVER_CIRCUIT_BREAKER_NAME = "searchNaverBlogApi"

@Component
internal class NaverBlogAPIImpl(
    val naverBlogAPIClient: NaverBlogAPIClient,
) : BlogAPIProvider {

    private val log = KotlinLogging.logger {}

    override fun search(searchDto: BlogSearchRequestDto): BlogResponseDto {

        val requestDto = NaverBlogRequestDto.of(searchDto)
        val response = this.naverBlogAPIClient.search(requestDto)

        log.debug { "response $response" }

        return response.toBlogResponseDto()
    }

    override fun getType(): ProviderType = ProviderType.Naver
}