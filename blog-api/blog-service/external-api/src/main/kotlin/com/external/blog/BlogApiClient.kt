package com.external.blog

import com.collector.event.BlogSearchEventDto
import com.collector.event.publisher.BlogEventPublisher
import com.external.blog.dto.BlogResponseDto
import com.external.blog.dto.BlogSearchRequestDto
import com.external.blog.provider.BlogAPIProvider
import com.external.blog.provider.ProviderType
import com.external.blog.provider.kakao.KAKAO_CIRCUIT_BREAKER_NAME
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.springframework.stereotype.Component
import kotlin.coroutines.coroutineContext

@Component
class BlogApiClient internal constructor(
    providers: List<BlogAPIProvider>,
    private val blogEventPublisher: BlogEventPublisher,
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
) {

    private val providers: Map<ProviderType, BlogAPIProvider> = providers
        .associateBy {
            it.getType()
        }

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun searchBlog(searchDto: BlogSearchRequestDto): BlogResponseDto {

        blogEventPublisher.sendSearchEvent(
            BlogSearchEventDto(keyword = searchDto.query)
        )

        return coroutineScope.async {

            if (isDisabledState()) {
                providers[ProviderType.Naver]!!.search(searchDto)
            } else {
                providers[ProviderType.Kakao]!!.search(searchDto)
            }
        }.await()
    }

    private fun isDisabledState(): Boolean {

        return circuitBreakerRegistry.circuitBreaker(KAKAO_CIRCUIT_BREAKER_NAME)
            .state == CircuitBreaker.State.OPEN
    }
}