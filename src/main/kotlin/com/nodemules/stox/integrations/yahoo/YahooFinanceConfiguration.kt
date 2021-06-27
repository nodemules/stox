package com.nodemules.stox.integrations.yahoo

import feign.Logger
import feign.RequestInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import java.net.URI

class YahooFinanceConfiguration(
    @Value("\${integrations.yahoo.api-key}") val apiKey: String,
    @Value("\${integrations.yahoo.host}") val host: String,
) {

    @Bean
    fun apiKeyRequestInterceptor(): RequestInterceptor = RequestInterceptor {
        it.header("X-RapidAPI-Key", apiKey)
        it.header("X-RapidAPI-Host", URI.create(host).host)
    }

    @Bean
    fun logLevel(): Logger.Level = Logger.Level.FULL
}