package com.nodemules.stox.quote

import com.nodemules.stox.failure.Failure
import io.vavr.control.Either

interface QuoteService {

    fun getQuote(symbol: String): Either<Failure, GlobalQuote>
}