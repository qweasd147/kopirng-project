package com.external.blog.provider.kakao

import com.external.blog.provider.ProviderProperties
import com.external.blog.provider.kakao.dto.KakaoBlogRequestDto
import com.external.blog.provider.kakao.dto.KakaoBlogResponseDto
import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.cloud.openfeign.SpringQueryMap
import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.GetMapping

@FeignClient(
    name = "kakaoBlogApiClient",
    url = "http://dapi.kakao.com",
    configuration = [KakaoAPIConfiguration::class]
)
internal interface KakaoBlogAPIClient {

    @GetMapping("/v2/search/blog")
    fun search(@SpringQueryMap searchDto: KakaoBlogRequestDto): KakaoBlogResponseDto
}

internal class KakaoAPIConfiguration(
    private val providerProperties: ProviderProperties,
) {

    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor { requestTemplate: RequestTemplate ->
            requestTemplate.header(
                "Authorization",
                "KakaoAK ${providerProperties.kakao}",
            )
        }
    }
}