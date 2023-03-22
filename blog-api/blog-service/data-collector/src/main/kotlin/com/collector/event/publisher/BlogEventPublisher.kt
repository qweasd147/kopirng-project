package com.collector.event.publisher

import com.collector.event.BlogSearchEventDto



interface BlogEventPublisher {

    /**
     * 검색 이벤트 발행
     */
    fun sendSearchEvent(searchEventDto: BlogSearchEventDto)
}