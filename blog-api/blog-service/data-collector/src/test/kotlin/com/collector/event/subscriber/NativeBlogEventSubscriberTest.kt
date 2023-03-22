package com.collector.event.subscriber

import com.blog.r2dbc.entity.SearchLog
import com.collector.domain.search.dto.SaveSearchLogRequest
import com.collector.domain.search.service.SearchLogService
import com.collector.event.BlogSearchEventDto
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class NativeBlogEventSubscriberTest : BehaviorSpec({

    val searchLogService: SearchLogService = mockk(relaxed = true)
    val blogEventSubscriber: BlogEventSubscriber by lazy {
        NativeBlogEventSubscriber(searchLogService)
    }

    Given("정상적인 event 객체로") {

        val log = "normalLog"
        val eventDto = BlogSearchEventDto(log)

        When("saveHistoryLog 함수를 호출하면") {

            val searchLogRequestSlot = slot<SaveSearchLogRequest>()
            coJustRun {
                searchLogService.saveLog(capture(searchLogRequestSlot))
            }
            blogEventSubscriber.saveHistoryLog(eventDto)

            Then("searchLogService.saveLog 함수가 호출 된다.") {

                coVerify(exactly = 1) {
                    searchLogService.saveLog(any())
                }
            }

            Then("saveLog로 넘겨주는 keyword와 event log 내용은 동일하다") {

                val searchLog = searchLogRequestSlot.captured.toSearchLog()

                (searchLog.contents == log) shouldBe true
            }
        }
    }

    Given("너무 긴 log를 갖는 event 객체로") {

        clearMocks(searchLogService)

        val log = createDummyString(SearchLog.MAX_CONTENTS_LENGTH + 1)
        val eventDto = BlogSearchEventDto(log)

        When("saveHistoryLog 함수를 호출하면") {

            blogEventSubscriber.saveHistoryLog(eventDto)

            Then("searchLogService.saveLog 함수는 호출 되지 않는다(skip)") {

                coVerify(exactly = 0) {

                    searchLogService.saveLog(any())
                }
            }
        }
    }
})

fun createDummyString(length: Int): String {
    return buildString {
        repeat(length) {
            append('a')
        }
    }
}