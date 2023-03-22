package com.external.blog.provider

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "apikey")
internal data class ProviderProperties internal constructor(
    val kakao: String,
    val naver: NaverKey,
)

internal data class NaverKey(
    var clientId: String,
    var clientSecret: String,
)