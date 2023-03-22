package com.collector.domain.search.service

import com.blog.r2dbc.entity.SearchLog
import com.blog.r2dbc.entity.SearchWord
import com.blog.r2dbc.repository.SearchLogRepository
import com.blog.r2dbc.repository.SearchWordRepository
import kotlinx.coroutines.flow.collect
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
internal class SearchWordCommandService(
    private val searchWordRepository: SearchWordRepository,
    private val searchLogRepository: SearchLogRepository,
) {

    private val log = KotlinLogging.logger {}

    /**
     * log 데이터를 기반으로 word를 저장한다
     *
     * chunk 커밋 처리하기 위하여 매번 새로운 transaction을 연다
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    suspend fun saveWords(targetItems: List<SearchLog>) {

        val searchLogByContents = targetItems.onEach {
            it.toFitContents()
        }.filter {
            val isEnable = it.contents.length <= SearchWord.MAX_WORD_LENGTH //길이가 너무 긴 것은 생략
            if (!isEnable) {
                log.debug { "너무 긴 컨텐츠는 생략 ${it.contents}" }
            }
            isEnable
        }.groupBy { it.contents }

        val requestWords = searchLogByContents.keys
        val alreadyExistsItems = searchWordRepository.findAllByWordIn(requestWords.toList())

        //기존에 존재하는 아이템에 대해서는 수량 증가
        increaseWordCount(alreadyExistsItems, searchLogByContents)

        val alreadyExistsWords = alreadyExistsItems.map { it.word }
        val notExistsItems = targetItems.filter { it.contents !in alreadyExistsWords }

        //존재하지 않던 아이템은 새롭게 생성
        createWord(notExistsItems)

        //log 데이터는 수집 완료 처리
        val collectedWord = targetItems.onEach {
            it.toCollected()
        }

        searchLogRepository.saveAll(collectedWord).collect()

        log.debug { "create or increase - $targetItems" }
    }


    private suspend fun increaseWordCount(
        existsWord: List<SearchWord>,
        searchLogByContents: Map<String, List<SearchLog>>,
    ) {

        existsWord.onEach {
            val countToIncrease = searchLogByContents[it.word]?.count() ?: 0
            it.increaseBy(countToIncrease)
        }

        //기존 존재하는 word는 각 count update
        searchWordRepository.saveAll(existsWord).collect()
        log.debug { "증가 완료 - $existsWord" }
    }

    private suspend fun createWord(targetItems: List<SearchLog>) {

        val searchLogsByContents: Map<String, List<SearchLog>> = targetItems.groupBy { it.contents }
        val notExistsWord = searchLogsByContents.map { (contentKey, logs) ->

            val totalCount = logs.size
            SearchWord(word = contentKey, count = totalCount.toLong())
        }

        searchWordRepository.saveAll(notExistsWord).collect()
        log.debug { "신규 등록 완료 - $notExistsWord" }
    }
}