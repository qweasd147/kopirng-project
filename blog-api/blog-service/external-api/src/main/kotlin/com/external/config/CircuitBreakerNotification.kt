package com.external.config

import com.external.blog.provider.kakao.KAKAO_CIRCUIT_BREAKER_NAME
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent
import io.github.resilience4j.core.EventConsumer
import mu.KotlinLogging
import org.springframework.context.annotation.Configuration

@Configuration
internal class CircuitBreakerNotification(
    circuitBreakerRegistry: CircuitBreakerRegistry,
) {

    private val log = KotlinLogging.logger {}

    init {

        //circuitBreakerRegistry.allCircuitBreakers -> not working
        circuitBreakerRegistry.circuitBreaker(KAKAO_CIRCUIT_BREAKER_NAME)
            .eventPublisher.onStateTransition(EventConsumer(::onStateTransition))

        log.info("서킷 브레이커 상태 변환 알림 이벤트 등록 완료")
    }

    fun onStateTransition(event: CircuitBreakerOnStateTransitionEvent) {
        val stateTransition = event.stateTransition

        if (stateTransition == CircuitBreaker.StateTransition.CLOSED_TO_OPEN) {
            log.warn("서킷 브레이커 열림 ${event.circuitBreakerName} - $stateTransition - ${event.creationTime}")
        } else {
            log.info("서킷 브레이커 상태 변환 ${event.circuitBreakerName} - $stateTransition - ${event.creationTime}")
        }

        //TODO : 서킷브래이커 open or close 되었을 때 장애 알림 처리
    }
}
