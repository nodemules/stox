package com.nodemules.stox.integrations

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDate

data class AlphaVantageGlobalQuote(
    @JsonProperty("01. symbol")
    val symbol: String,
    @JsonProperty("02. open")
    val open: BigDecimal,
    @JsonProperty("03. high")
    val high: BigDecimal,
    @JsonProperty("04. low")
    val low: BigDecimal,
    @JsonProperty("05. price")
    val price: BigDecimal,
    @JsonProperty("06. volume")
    val volume: Long,
    @JsonProperty("07. latest trading day")
    val latestTradingDay: LocalDate,
    @JsonProperty("08. previous close")
    val previousClose: BigDecimal,
    @JsonProperty("09. change")
    val change: BigDecimal,
    @JsonProperty("10. change percent")
    val changePercent: String
)