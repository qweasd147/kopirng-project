package com.external

import com.collector.EnableBlogCollector
import com.external.blog.provider.ProviderProperties
import mu.KotlinLogging
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime

@ComponentScan(basePackageClasses = [ExternalApiConfiguration::class])
@Configuration
@EnableFeignClients
@EnableConfigurationProperties(ProviderProperties::class)
@EnableBlogCollector
class ExternalApiConfiguration : ApplicationRunner {

    private val log = KotlinLogging.logger {}

    override fun run(args: ApplicationArguments?) {

        log.debug("start ExternalApiConfiguration ${LocalDateTime.now()} ")
    }
}