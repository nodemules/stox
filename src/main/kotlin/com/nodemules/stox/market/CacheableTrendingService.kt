package com.nodemules.stox.market

import com.nodemules.stox.failure.Failure
import com.nodemules.stox.failure.GenericFailure
import com.nodemules.stox.integrations.yahoo.YahooFinanceMarketClient
import com.nodemules.stox.integrations.yahoo.YahooQuote
import com.nodemules.stox.quote.GlobalQuote
import io.vavr.control.Either
import mu.KLogging
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import java.math.RoundingMode

@Component
class CacheableTrendingService(
    val yahooFinanceMarketClient: YahooFinanceMarketClient,
) : TrendingService {

    @Cacheable("trendingCache")
    override fun getTrending(): Either<Failure, List<GlobalQuote>> =
        yahooFinanceMarketClient.getTrendingTickers()
            .map { it.finance.result.firstOrNull() }
            .map { it?.quotes ?: emptyList() }
            .map { quotes -> quotes.map { it.toGlobalQuote() } }
            .peek { BasicMarketService.logger.info { "Found ${it.size} trending tickers" } }
            .flatMap { if (it.isEmpty()) Either.left(GenericFailure("FAIL")) else Either.right(it) }

    companion object : KLogging() {

        fun YahooQuote.toGlobalQuote() = GlobalQuote(
            quoteType = this.quoteType,
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
