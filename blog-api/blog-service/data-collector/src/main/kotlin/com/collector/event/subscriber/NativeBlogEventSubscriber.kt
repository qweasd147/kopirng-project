package com.collector.event.subscriber

import com.blog.r2dbc.entity.SearchLog
import com.collector.domain.search.dto.SaveSearchLogRequest
import com.collector.domain.search.service.SearchLogService
import com.collector.event.BlogSearchEventDto
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
internal class NativeBlogEventSubscriber(
    private val searchLogService: SearchLogService,
) : BlogEventSubscriber {

    private val log = KotlinLogging.logger {}
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @EventListener
    @Async
    override fun saveHistoryLog(searchEventDto: BlogSearchEventDto) {

        log.debug { "수신 $searchEventDto" }

        if (searchEventDto.keyword.trim().length > SearchLog.MAX_CONTENTS_LENGTH) {

            log.debug { "너무 긴 데이터 skip $searchEventDto" }
            return
        }

        coroutineScope.launch {

            val saveRequestDto = SaveSearchLogRequest(
                contents = searchEventDto.keyword, createdAt = searchEventDto.publishedAt
            )
            searchLogService.saveLog(saveRequestDto)
        }
    }
}