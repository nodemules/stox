package com.nodemules.stox.integrations.yahoo

import com.nodemules.stox.failure.Failure
import com.nodemules.stox.failure.HttpFailure
import com.nodemules.stox.integrations.YahooTrendingTickersResponse
import feign.FeignException
import feign.hystrix.FallbackFactory
import io.vavr.control.Either
import mu.KLogging
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "yahooFinanceMarketClient",
    url = "\${integrations.yahoo.host}/market",
    configuration = [YahooFinanceConfiguration::class],
    fallbackFactory = YahooFinanceMarketClient.YahooFinanceMarketClientFallbackFactory::class
)
interface YahooFinanceMarketClient {

    @GetMapping("/get-trending-tickers")
    fun getTrendingTickers(): Either<Failure, YahooTrendingTickersResponse>

    @GetMapping("/get-spark")
    fun getSparks(@RequestParam symbols: Array<String>): Either<Failure, Map<String, Spark>>

    @Component
    class YahooFinanceMarketClientFallbackFactory : FallbackFactory<YahooFinanceMarketClient> {
        override fun create(cause: Throwable?): YahooFinanceMarketClient = object : YahooFinanceMarketClient {
            override fun getTrendingTickers(): Either<Failure, YahooTrendingTickersResponse> =
                left(cause, "An error occurred getting TRENDING_TICKERS")

            override fun getSparks(symbols: Array<String>): Either<Failure, Map<String, Spark>> =
                left(cause, "An error occurred getting SPARK for $symbols")

        }

        companion object : KLogging() {

            fun <T> left(cause: Throwable?, errorMessage: String?): Either<Failure, T> {
                logger.error(cause) { errorMessage }
                return when (cause) {
                    is FeignException ->
                        HttpStatus.valueOf(cause.status())
                            .let {
                                if (it.is2xxSuccessful) HttpStatus.INTERNAL_SERVER_ERROR
                                else it
                            }
                            .let { Either.left(HttpFailure(it, errorMessage)) }
                    else -> Either.left(HttpFailure(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage))
                }
            }
        }
    }
}

