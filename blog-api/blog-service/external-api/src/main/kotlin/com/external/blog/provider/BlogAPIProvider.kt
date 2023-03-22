package com.external.blog.provider

import com.external.blog.dto.BlogResponseDto
import com.external.blog.dto.BlogSearchRequestDto

internal interface BlogAPIProvider {

    /**
     * 조회 api 호출
     */
    fun search(searchDto: BlogSearchRequestDto): BlogResponseDto

    fun getType(): ProviderType
}

internal enum class ProviderType {
    Kakao,
    Naver,
}