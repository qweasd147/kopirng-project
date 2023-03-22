package com.external.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import feign.codec.Decoder
import feign.codec.Encoder
import feign.form.spring.SpringFormEncoder
import org.springframework.beans.factory.ObjectFactory
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder
import org.springframework.cloud.openfeign.support.SpringDecoder
import org.springframework.cloud.openfeign.support.SpringEncoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter


@Configuration
internal class FeignConfig {

    private val messageConverters = ObjectFactory { HttpMessageConverters() }

    @Bean
    fun feignFormEncoder(): Encoder {
        return SpringFormEncoder(SpringEncoder(messageConverters))
    }

    @Bean
    fun feignDecoder(): Decoder {
        val jacksonConverter: HttpMessageConverter<*> = MappingJackson2HttpMessageConverter(customObjectMapper())
        val objectFactory =
            ObjectFactory { HttpMessageConverters(jacksonConverter) }
        return ResponseEntityDecoder(SpringDecoder(objectFactory))
    }
}


private fun customObjectMapper(): ObjectMapper {
    val objectMapper = ObjectMapper()
    objectMapper.propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    return objectMapper
}