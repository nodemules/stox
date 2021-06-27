package com.nodemules.stox.quote

import java.math.BigDecimal
import java.time.LocalDate

data class GlobalQuote(
    val symbol: String,
    val open: BigDecimal,
    val high: BigDecimal? = null,
    val low: BigDecimal? = null,
    val price: BigDecimal,
    val volume: Long? = null,
    val latestTradingDay: LocalDate,
    val previousClose: BigDecimal,
    val change: BigDecimal,
    val changePercent: String
)
