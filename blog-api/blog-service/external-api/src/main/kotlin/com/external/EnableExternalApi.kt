package com.external

import org.springframework.context.annotation.Import

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Import(ExternalApiConfiguration::class)
annotation class EnableExternalApi
