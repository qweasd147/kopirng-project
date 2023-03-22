package com.collector

import com.blog.r2dbc.EnableR2dbcData
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableAsync

@ComponentScan(basePackageClasses = [BlogCollectorApplication::class])
@EnableAsync
@EnableR2dbcData
class BlogCollectorApplication