package com.external.blog

import com.collector.event.publisher.BlogEventPublisher
import com.external.blog.provider.BlogAPIProvider
import com.external.blog.provider.ProviderType
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(MockKExtension::class)
internal class BlogApiClientTest : BehaviorSpec({

    val providers: List<BlogAPIProvider> = listOf(
        mockk(relaxed = true) { every { getType() } returns ProviderType.Kakao },
        mockk(relaxed = true) { every { getType() } returns ProviderType.Naver },
    )

    val blogEventPublisher = mockk<BlogEventPublisher>(relaxed = true, relaxUnitFun = true)
    val circuitBreakerRegistry = mockk<CircuitBreakerRegistry>()
    val blogApiClient: BlogApiClient by lazy {
        BlogApiClient(
            providers = providers,
            blogEventPublisher = blogEventPublisher,
            circuitBreakerRegistry = circuitBreakerRegistry,
        )
    }

    Given("'circuit breaker' 가 close 상태 일 때") {

        clearMocks(blogEventPublisher, circuitBreakerRegistry)
        every {
            circuitBreakerRegistry.circuitBreaker(any())
        } returns mockk {
            every { state } returns CircuitBreaker.State.CLOSED
        }
        justRun { blogEventPublisher.sendSearchEvent(any()) }

        When("블로그 조회 함수(searchBlog)를 호출 하면") {

            blogApiClient.searchBlog(mockk(relaxed = true))

            Then("KakaoProvider에서 검색API 함수를 호출한다") {
                val kakaoProvider = providers.first { it.getType() == ProviderType.Kakao }
                verify(exactly = 1) { kakaoProvider.search(any()) }
            }

            Then("검색 이벤트를 발행한다") {
                verify(exactly = 1) { blogEventPublisher.sendSearchEvent(any()) }
            }
        }
    }

    Given("'circuit breaker' 가 open 상태 일 때") {

        clearMocks(blogEventPublisher, circuitBreakerRegistry)
        every {
            circuitBreakerRegistry.circuitBreaker(any())
        } returns mockk {
            every { state } returns CircuitBreaker.State.OPEN
        }
        justRun { blogEventPublisher.sendSearchEvent(any()) }

        When("블로그 조회 함수(searchBlog)를 호출 하면") {

            blogApiClient.searchBlog(mockk(relaxed = true))

            Then("NaverProvider에서 검색API 함수를 호출한다") {
                val naverProvider = providers.first { it.getType() == ProviderType.Naver }
                verify(exactly = 1) { naverProvider.search(any()) }
            }

            Then("검색 이벤트를 발행한다") {
                verify(exactly = 1) { blogEventPublisher.sendSearchEvent(any()) }
            }
        }
    }
})