package com.blog.r2dbc

import org.springframework.context.annotation.Import

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Import(R2dbcCoreConfiguration::class)
annotation class EnableR2dbcData
