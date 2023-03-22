package com.external.blog.provider.naver

import com.external.blog.provider.ProviderProperties
import com.external.blog.provider.naver.dto.NaverBlogRequestDto
import com.external.blog.provider.naver.dto.NaverBlogResponseDto
import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.cloud.openfeign.SpringQueryMap
import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.GetMapping

@FeignClient(
    name = "naverBlogApiClient",
    url = "https://openapi.naver.com",
    configuration = [NaverAPIConfiguration::class]
)
internal interface NaverBlogAPIClient {

    @GetMapping("/v1/search/blog.json")
    fun search(@SpringQueryMap searchDto: NaverBlogRequestDto): NaverBlogResponseDto
}

internal class NaverAPIConfiguration(
    private val providerProperties: ProviderProperties,
) {

    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor { requestTemplate: RequestTemplate ->
            requestTemplate.header(
                "X-Naver-Client-Id",
                providerProperties.naver.clientId,
            )

            requestTemplate.header(
                "X-Naver-Client-Secret",
                providerProperties.naver.clientSecret,
            )
        }
    }
}