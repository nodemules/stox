package com.nodemules.stox.integrations

import com.nodemules.stox.failure.Failure
import com.nodemules.stox.failure.HttpFailure
import com.nodemules.stox.integrations.AlphaVantageStockTimeSeriesClient.AlphaVantageStockTimeSeriesClientConfiguration
import com.nodemules.stox.integrations.AlphaVantageStockTimeSeriesClient.AlphaVantageStockTimeSeriesClientFallbackFactory
import feign.FeignException
import feign.Logger
import feign.RequestInterceptor
import feign.hystrix.FallbackFactory
import io.vavr.control.Either
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "alphaVantageStockTimeSeriesClient",
    url = "\${integrations.alpha-vantage.host:https://www.alphavantage.co}",
    configuration = [AlphaVantageStockTimeSeriesClientConfiguration::class],
    fallbackFactory = AlphaVantageStockTimeSeriesClientFallbackFactory::class
)
interface AlphaVantageStockTimeSeriesClient {

    @GetMapping("/query?function=GLOBAL_QUOTE", produces = ["application/json"])
    fun getGlobalQuote(@RequestParam symbol: String): Either<Failure, AlphaVantageGlobalQuoteResponse>

    class AlphaVantageStockTimeSeriesClientConfiguration(
        @Value("\${integrations.alpha-vantage.api-key}") val apiKey: String
    ) {

        @Bean
        fun apiKeyRequestInterceptor(): RequestInterceptor = RequestInterceptor {
            it.query("apikey", apiKey)
        }

        @Bean
        fun logLevel(): Logger.Level = Logger.Level.FULL
    }

    @Component
    class AlphaVantageStockTimeSeriesClientFallbackFactory : FallbackFactory<AlphaVantageStockTimeSeriesClient> {
        override fun create(cause: Throwable?): AlphaVantageStockTimeSeriesClient = object : AlphaVantageStockTimeSeriesClient {
            override fun getGlobalQuote(symbol: String): Either<Failure, AlphaVantageGlobalQuoteResponse> {
                val errorMessage = "An error occurred getting GLOBAL_QUOTE for $symbol"
                logger.error(cause) { errorMessage }
                return when (cause) {
                    is FeignException -> Either.left(HttpFailure(HttpStatus.valueOf(cause.status()), errorMessage))
                    else -> Either.left(HttpFailure(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage))
                }
            }


        }

        companion object : KLogging()
    }

}