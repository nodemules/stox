package com.nodemules.stox.integrations

import com.nodemules.stox.integrations.yahoo.YahooQuote

data class YahooTrendingTickersResponse(
    val finance: Finance
) {

    data class Finance(
        val result: List<Result>
    ) {

        data class Result(
            val count: Int,
            val quotes: List<YahooQuote>,
            val jobTimestamp: Long,
            val startInterval: Long
        )
    }
}