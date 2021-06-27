package com.nodemules.stox.quote

import com.nodemules.stox.failure.Failure
import com.nodemules.stox.integrations.AlphaVantageGlobalQuote
import com.nodemules.stox.integrations.AlphaVantageStockTimeSeriesClient
import io.vavr.control.Either
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class BasicQuoteService(
    val alphaVantageStockTimeSeriesClient: AlphaVantageStockTimeSeriesClient
) : QuoteService {

    override fun getQuote(symbol: String): Either<Failure, GlobalQuote> = alphaVantageStockTimeSeriesClient.getGlobalQuote(symbol)
        .peek { logger.info { "Found quote for $symbol" } }
        .peekLeft { logger.error { "Failed to find quote for $symbol because ${it.reason}" } }
        .map { it.globalQuote.toGlobalQuote() }

    companion object : KLogging() {
        fun AlphaVantageGlobalQuote.toGlobalQuote() = GlobalQuote(
            symbol = this.symbol,
            open = this.open,
            high = this.high,
            low = this.low,
            price = this.price,
            volume = this.volume,
            latestTradingDay = this.latestTradingDay,
            previousClose = this.previousClose,
            change = this.change,
            changePercent = this.changePercent
        )
    }
}