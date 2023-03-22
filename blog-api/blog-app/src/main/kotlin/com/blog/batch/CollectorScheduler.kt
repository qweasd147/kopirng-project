package com.blog.batch

import com.collector.domain.search.service.SearchWordBulkService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled

@Configuration
class CollectorScheduler(
    private val searchWordBulkService: SearchWordBulkService,
) {

    //5초 마다 수집
    @Scheduled(fixedDelay = 5000)
    fun collectWord() {

        CoroutineScope(Dispatchers.Default).launch {
            searchWordBulkService.collectByLogs()
        }
    }
}