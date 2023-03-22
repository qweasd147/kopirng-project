package com.external.blog.provider.kakao

import com.external.blog.provider.BlogAPIProvider
import com.external.blog.dto.BlogResponseDto
import com.external.blog.dto.BlogSearchRequestDto
import com.external.blog.provider.ProviderType
import com.external.blog.provider.kakao.dto.KakaoBlogRequestDto
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import mu.KotlinLogging
import org.springframework.stereotype.Component

const val KAKAO_CIRCUIT_BREAKER_NAME = "searchKakaoBlogApi"

@Component
internal class KakaoBlogAPIImpl(
    val kakaoBlogAPIClient: KakaoBlogAPIClient,
) : BlogAPIProvider {

    private val log = KotlinLogging.logger {}

    @CircuitBreaker(name = KAKAO_CIRCUIT_BREAKER_NAME)
    override fun search(searchDto: BlogSearchRequestDto): BlogResponseDto {

        val requestDto = KakaoBlogRequestDto.of(searchDto)
        val response = this.kakaoBlogAPIClient.search(requestDto)
        log.debug { "response $response" }

        return response.toBlogResponseDto()
    }

    override fun getType(): ProviderType = ProviderType.Kakao
}