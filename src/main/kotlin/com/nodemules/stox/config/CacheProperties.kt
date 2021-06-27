package com.nodemules.stox.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.Min

@Validated
@ConstructorBinding
@ConfigurationProperties("cache")
data class CacheProperties(
    @Min(0)
    val ttl: Long = 0
)