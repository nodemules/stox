package com.nodemules.stox.quote

import java.math.BigDecimal
import java.time.LocalDate

data class GlobalQuote(
    val symbol: String,
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val price: BigDecimal,
    val volume: Long,
    val latestTradingDay: LocalDate,
    val previousClose: BigDecimal,
    val change: BigDecimal,
    val changePercent: String
)
