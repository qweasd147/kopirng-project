package com.blog.r2dbc.entity

import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("search_log")
class SearchLog(
    contents: String,
) : BaseEntity() {

    init {
        if (contents.trim().length > MAX_CONTENTS_LENGTH) {
            throw IllegalArgumentException("검색어 길이는 ${MAX_CONTENTS_LENGTH}이 넘어 갈 수 없습니다.")
        }
    }

    var contents: String = contents.trim()
        protected set

    var isCollected: Boolean = false
        protected set

    fun updateCreatedAt(createdAt: LocalDateTime) {
        this.createdAt = createdAt
    }

    fun toCollected() {
        this.isCollected = true
    }

    fun toFitContents() {
        this.contents = this.contents.trim()
    }

    override fun toString(): String {
        return "SearchLog(idx='$idx', contents='$contents', isCollected=$isCollected)"
    }

    companion object {
        const val MAX_CONTENTS_LENGTH: Int = 150
    }


}