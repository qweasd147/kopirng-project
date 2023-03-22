package com.blog.r2dbc.entity

import org.springframework.data.relational.core.mapping.Table

@Table("search_word")
class SearchWord(
    word: String,
    count: Long = 1,
) : BaseEntity() {

    init {
        if (word.trim().length > MAX_WORD_LENGTH) {
            throw IllegalArgumentException("저장 되는 단어 길이는 ${MAX_WORD_LENGTH}자 이하만 가능합니다.")
        }
    }

    var word: String = word.trim()
        protected set

    var count: Long = count
        protected set

    fun increaseBy(countToIncrease: Int) {
        this.count += countToIncrease
    }

    override fun toString(): String {
        return "SearchWord(word='$word', count=$count)"
    }

    companion object {
        const val MAX_WORD_LENGTH: Int = 30
    }
}