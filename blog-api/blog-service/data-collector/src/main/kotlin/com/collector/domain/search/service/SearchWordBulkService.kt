package com.collector.domain.search.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SearchWordBulkService internal constructor(
    private val searchWordCommandService: SearchWordCommandService,
    private val searchLogService: SearchLogService,
) {

    private val log = KotlinLogging.logger {}

    suspend fun collectByLogs() {

        val limitSize = 50
        var unCollectedTarget = this.searchLogService.findAllByUnCollected(limitSize)
        var collectedCount = 0L

        while (true) {
            if (unCollectedTarget.isEmpty()) break

            this.searchWordCommandService.saveWords(unCollectedTarget)

            collectedCount += unCollectedTarget.size
            val minIdx = unCollectedTarget.mapNotNull { it.idx }
                .minBy { it }

            //실시간으로 계속 쌓여, 끝도 없이 돌아가는 것을 방지 하기 위해 target 중 가장 작은 idx 미만인 데이터만 처리
            unCollectedTarget = this.searchLogService.findAllByUnCollected(limitSize, minIdx)
        }

        log.info { "$collectedCount 건 수집 완료" }
    }
}