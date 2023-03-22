package com.blog

import com.collector.EnableBlogCollector
import com.external.EnableExternalApi
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableExternalApi
@EnableBlogCollector
@EnableScheduling
class BlogApiApplication

fun main(args: Array<String>) {
    runApplication<BlogApiApplication>(*args)
}
