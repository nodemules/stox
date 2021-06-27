package com.nodemules.stox.market

import com.nodemules.stox.failure.Failure
import com.nodemules.stox.integrations.yahoo.Spark
import com.nodemules.stox.integrations.yahoo.YahooQuote
import com.nodemules.stox.quote.GlobalQuote
import io.vavr.control.Either

interface MarketService {

    fun getTrending(quoteType: YahooQuote.QuoteType? = null): Either<Failure, List<GlobalQuote>>

    fun getSparks(symbols: List<String>): Either<Failure, List<Spark>>
}