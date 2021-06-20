package com.nodemules.stox.quote

import com.nodemules.stox.failure.Failure
import io.vavr.control.Either
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/quote")
class QuoteController(
    val quoteService: QuoteService
) {

    @GetMapping("/{symbol}")
    fun getQuote(@PathVariable symbol: String): Either<Failure, GlobalQuote> = quoteService.getQuote(symbol)
}