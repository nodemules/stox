package com.nodemules.stox.market

import com.nodemules.stox.extensions.toEither
import com.nodemules.stox.failure.Failure
import com.nodemules.stox.integrations.yahoo.Spark
import com.nodemules.stox.integrations.yahoo.YahooFinanceMarketClient
import com.nodemules.stox.quote.GlobalQuote
import io.vavr.control.Either
import mu.KLogging
import org.springframework.stereotype.Service
import java.util.function.Function
import java.util.stream.Collectors

@Service
class BasicMarketService(
    val yahooFinanceMarketClient: YahooFinanceMarketClient,
    val trendingService: TrendingService,
    val sparkDummyCache: SparkDummyCache
) : MarketService {

    override fun getTrending(quoteType: String?): Either<Failure, List<GlobalQuote>> =
        trendingService.getTrending()
            .map { result ->
                logger.info { result.map { it.quoteType }.distinct() }
                result.filter { quoteType == null || it.quoteType == quoteType }
            }

    override fun getSparks(symbols: List<String>): Either<Failure, List<Spark>> =
        symbols.parallelStream()
            .map { sparkDummyCache.get(it).orNull }
            .filter { it != null }
            .collect(Collectors.toMap({ it?.symbol }, Function.identity()))
            .let { cached ->
                cached.keys
                    .let { keys -> symbols.parallelStream().filter { !keys.contains(it) }.collect(Collectors.toList()).toTypedArray() }
                    .toEither()
                    .peek { if (it.isNotEmpty()) logger.info { "Fetching data for ${it.size} sparks [${it.joinToString(separator = ", ")}]" } }
                    .flatMap { if (it.isNotEmpty()) yahooFinanceMarketClient.getSparks(it) else Either.right(emptyMap()) }
                    .map { map -> map.values.stream().peek { sparkDummyCache.put(it) }.collect(Collectors.toList()) }
                    .peek { if (it.isNotEmpty()) logger.info { "Found ${it.size} new sparks for $symbols" } }
                    .peek { it.addAll(cached.values); logger.info { "Found ${cached.size} sparks in cache" } }
                    .peekLeft { logger.error { "Sparks not found for $symbols because ${it.reason}" } }
            }

    companion object : KLogging()
}
