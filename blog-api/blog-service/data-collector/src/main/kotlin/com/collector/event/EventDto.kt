package com.collector.event

import java.time.LocalDateTime

data class BlogSearchEventDto(

    val keyword: String,
    val publishedAt: LocalDateTime = LocalDateTime.now(),
)