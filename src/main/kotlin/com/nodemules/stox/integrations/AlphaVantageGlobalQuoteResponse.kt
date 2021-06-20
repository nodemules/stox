package com.nodemules.stox.integrations

import com.fasterxml.jackson.annotation.JsonProperty

data class AlphaVantageGlobalQuoteResponse(
    @JsonProperty("Global Quote")
    val globalQuote: AlphaVantageGlobalQuote
)