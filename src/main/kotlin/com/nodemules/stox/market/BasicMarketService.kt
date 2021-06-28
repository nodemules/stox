package com.nodemules.stox.market

import com.nodemules.stox.extensions.toEither
import com.nodemules.stox.failure.Failure
import com.nodemules.stox.failure.GenericFailure
import com.nodemules.stox.integrations.yahoo.Spark
import com.nodemules.stox.integrations.yahoo.YahooFinanceMarketClient
import com.nodemules.stox.integrations.yahoo.YahooQuote
import com.nodemules.stox.quote.GlobalQuote
import io.vavr.control.Either
import mu.KLogging
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.math.RoundingMode
import java.util.function.Function
import java.util.stream.Collectors

@Service
class BasicMarketService(
    val yahooFinanceMarketClient: YahooFinanceMarketClient,
    val sparkDummyCache: SparkDummyCache
) : MarketService {

    @Cacheable("trendingCache")
    override fun getTrending(quoteType: YahooQuote.QuoteType?): Either<Failure, List<GlobalQuote>> =
        yahooFinanceMarketClient.getTrendingTickers()
            .map { it.finance.result.firstOrNull() }
            .peek { logger.info { "Found ${it?.count ?: 0} trending tickers" } }
            .flatMap { if (it == null) Either.left(GenericFailure("FAIL")) else Either.right(it) }
            .map { result ->
                result.quotes.filter { quoteType == null || it.quoteType == quoteType.name }.map { quote -> quote.toGlobalQuote() }
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

    companion object : KLogging() {

        fun YahooQuote.toGlobalQuote() = GlobalQuote(
            symbol = this.symbol,
            open = this.open,
            price = this.regularMarketPrice,
            latestTradingDay = this.regularMarketTime.withZoneSameLocal(exchangeTimezoneName).toLocalDate(),
            previousClose = this.regularMarketPreviousClose,
            change = this.regularMarketChange,
            changePercent = "${this.regularMarketChangePercent.setScale(2, RoundingMode.HALF_DOWN)}%"
        )
    }
}