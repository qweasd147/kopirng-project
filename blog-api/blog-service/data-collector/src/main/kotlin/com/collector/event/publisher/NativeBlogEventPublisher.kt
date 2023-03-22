package com.collector.event.publisher

import com.collector.event.BlogSearchEventDto
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
internal class NativeBlogEventPublisher(
    private val eventPublisher: ApplicationEventPublisher,
) : BlogEventPublisher {

    override fun sendSearchEvent(searchEventDto: BlogSearchEventDto) {
        eventPublisher.publishEvent(searchEventDto)
    }
}