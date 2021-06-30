package com.nodemules.stox.integrations.yahoo

import java.math.BigDecimal
import java.time.ZoneId
import java.time.ZonedDateTime

data class YahooQuote(
    val quoteType: String,
    val quoteSourceName: String?,

    val symbol: String,
    val shortName: String? = null,
    val longName: String? = null,

    val regularMarketPrice: BigDecimal,
    val regularMarketTime: ZonedDateTime,
    val regularMarketChange: BigDecimal,
    val regularMarketChangePercent: BigDecimal,
    val regularMarketPreviousClose: BigDecimal,

    val market: String,
    val marketState: String,
    val fullExchangeName: String,
    val exchange: String,
    val exchangeDataDelayedBy: Int,
    val exchangeTimezoneName: ZoneId,

    val sourceInterval: Long,

    val priceHint: Int,

    val tradeable: Boolean,

    val triggerable: Boolean,
    val esgPopulated: Boolean,

    val language: String,
    val region: String
) {
    val open = regularMarketPrice + regularMarketChange
}
