package com.collector

import org.springframework.context.annotation.Import

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Import(BlogCollectorApplication::class)
annotation class EnableBlogCollector