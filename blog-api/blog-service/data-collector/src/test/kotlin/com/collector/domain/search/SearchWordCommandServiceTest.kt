package com.collector.domain.search

import com.blog.r2dbc.entity.SearchLog
import com.blog.r2dbc.entity.SearchWord
import com.blog.r2dbc.repository.SearchLogRepository
import com.blog.r2dbc.repository.SearchWordRepository
import com.collector.domain.search.service.SearchWordCommandService
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.flow.collect
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyList

@ExtendWith(MockKExtension::class)
class SearchWordCommandServiceTest : BehaviorSpec({

    val searchWordRepository = mockk<SearchWordRepository>(relaxed = true)
    val searchLogRepository = mockk<SearchLogRepository>(relaxed = true)
    val commandService: SearchWordCommandService by lazy {
        SearchWordCommandService(searchWordRepository, searchLogRepository)
    }

    val requestWords = mapOf(
        "word1" to 5, "word2" to 1, "word3" to 3
    )

    Given("전부 새로운 검색어를") {

        val searchLogs = toSearchLogFixture(requestWords)
        val wordSlot = slot<List<SearchWord>>()

        //저장 된 내역에 없음
        coEvery { searchWordRepository.findAllByWordIn(any()) } returns listOf()
        coJustRun { searchWordRepository.saveAll(capture(wordSlot)).collect() }

        When("단어 별 저장하면(saveWords)") {
            commandService.saveWords(searchLogs)

            Then("word 저장 함수가 호출 된다.") {

                coVerify(atLeast = 1) { searchWordRepository.saveAll(anyList()) }
            }

            Then("넘겨 받은 단어 내 중복 개수 만큼 count 되어 저장 된다.") {

                wordSlot.captured.forEach {
                    //캡쳐 된 count == 요청 된 word count
                    it.count shouldBe requestWords[it.word]
                }
            }

            Then("log는 전부 수집 완료 처리(collected) 된다") {

                searchLogs.forEach {
                    it.isCollected shouldBe true
                }
            }
        }
    }


    Given("이미 저장 된 검색어를") {

        clearMocks(searchWordRepository, searchLogRepository)

        val searchLogs = toSearchLogFixture(requestWords)
        val existsWord = mapOf(
            "word1" to 2, "word2" to 5, "word3" to 8
        )

        val alreadyExistsWord = existsWord.map {
            SearchWord(word = it.key, count = it.value.toLong())
        }

        val wordSlot = slot<List<SearchWord>>()

        //중복 되는 단어가 있음
        coEvery { searchWordRepository.findAllByWordIn(any()) } returns alreadyExistsWord
        coJustRun { searchWordRepository.saveAll(capture(wordSlot)).collect() }

        When("단어 별 저장하면(saveWords)") {

            commandService.saveWords(searchLogs)

            Then("word 저장 함수가 호출 된다.") {
                coVerify(atLeast = 1) { searchWordRepository.saveAll(anyList()) }
            }

            Then("요청 한 개수 만큼 count가 증가 되어 저장 된다") {

                wordSlot.captured.forEach {

                    val oldWordCount = alreadyExistsWord.first { oldWord -> oldWord.word == it.word }.count
                    //capture된 count == 기존 저장 된 count + 요청 count
                    it.count shouldBe (requestWords[it.word]!!.toLong() + oldWordCount)
                }
            }

            Then("log는 전부 수집 완료 처리(collected) 된다") {

                searchLogs.forEach {
                    it.isCollected shouldBe true
                }
            }
        }
    }
})

fun toSearchLogFixture(wordsInfo: Map<String, Int>): List<SearchLog> {

    return wordsInfo.flatMap { entry ->
        List(entry.value) { SearchLog(entry.key) }
    }
}