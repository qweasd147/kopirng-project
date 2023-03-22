package com.blog.r2dbc.entity

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(MockKExtension::class)  //mockk는 필요없지만 ide 테스트 인식 문제로 추가
class SearchWordTest : BehaviorSpec({

    Given("길이가 ${SearchWord.MAX_WORD_LENGTH}이 넘는 데이터로") {

        val tryWord = createDummyContent(SearchWord.MAX_WORD_LENGTH + 1)

        When("SearchWord 객체 생성을 시도하면 ") {
            Then("'IllegalArgumentException'이 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    SearchWord(tryWord)
                }
            }
        }
    }

    Given("길이가 ${SearchWord.MAX_WORD_LENGTH}이하 데이터로") {

        val tryWord = createDummyContent(SearchWord.MAX_WORD_LENGTH)

        When("SearchWord 객체 생성을 시도하면 ") {
            Then("정상적으로 생성 가능하다(exception 발생 x)") {
                shouldNotThrowAny {
                    SearchWord(tryWord)
                }
            }
        }
    }
})