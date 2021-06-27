package com.nodemules.stox.integrations.yahoo

import java.math.BigDecimal

data class Spark(
    val symbol: String,
    val timestamp: Array<Long>,
    val close: Array<BigDecimal>,
    val chartPreviousClose: Int,
    val end: Int,
    val start: Int,
    val previousClose: Int,
    val dataGranularity: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Spark

        if (symbol != other.symbol) return false
        if (!timestamp.contentEquals(other.timestamp)) return false
        if (!close.contentEquals(other.close)) return false
        if (chartPreviousClose != other.chartPreviousClose) return false
        if (end != other.end) return false
        if (start != other.start) return false
        if (previousClose != other.previousClose) return false
        if (dataGranularity != other.dataGranularity) return false

        return true
    }

    override fun hashCode(): Int {
        var result = symbol.hashCode()
        result = 31 * result + timestamp.contentHashCode()
        result = 31 * result + close.contentHashCode()
        result = 31 * result + chartPreviousClose
        result = 31 * result + end
        result = 31 * result + start
        result = 31 * result + previousClose
        result = 31 * result + dataGranularity
        return result
    }
}