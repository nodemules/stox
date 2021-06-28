package com.nodemules.stox.market

import com.nodemules.stox.failure.Failure
import com.nodemules.stox.quote.GlobalQuote
import io.vavr.control.Either

interface TrendingService {

    fun getTrending(): Either<Failure, List<GlobalQuote>>
}