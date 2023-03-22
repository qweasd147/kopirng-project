package com.blog.r2dbc.entity

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(MockKExtension::class)  //mockk는 필요없지만 ide 테스트 인식 문제로 추가
class SearchLogTest : BehaviorSpec({

    Given("길이가 ${SearchLog.MAX_CONTENTS_LENGTH}이 넘는 데이터로") {

        //길이가 151인 문자열
        val tryContents = createDummyContent(SearchLog.MAX_CONTENTS_LENGTH + 1)

        When("SearchLog 객체 생성을 시도하면 ") {
            Then("'IllegalArgumentException'이 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    SearchLog(tryContents)
                }
            }
        }
    }

    Given("길이가 ${SearchLog.MAX_CONTENTS_LENGTH}이하 데이터로") {

        //길이가 150인 문자열
        val tryContents = createDummyContent(SearchLog.MAX_CONTENTS_LENGTH)

        When("SearchLog 객체 생성을 시도하면 ") {
            Then("정상적으로 생성 가능하다(exception 발생 x)") {
                shouldNotThrowAny {
                    SearchLog(tryContents)
                }
            }
        }
    }
})

fun createDummyContent(length: Int): String {
    return buildString {
        repeat(length) {
            append('a')
        }
    }
}