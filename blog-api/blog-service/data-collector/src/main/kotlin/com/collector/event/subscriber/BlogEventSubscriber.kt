package com.collector.event.subscriber

import com.collector.event.BlogSearchEventDto

internal interface BlogEventSubscriber {

    fun saveHistoryLog(searchEventDto: BlogSearchEventDto)
}