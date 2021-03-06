package com.nodemules.stox.market

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/market")
class MarketController(
    val marketService: MarketService
) {

    @GetMapping("/trending")
    fun getTrending(@RequestParam(required = false) quoteType: String?) = marketService.getTrending(quoteType)

    @GetMapping("/sparks")
    fun getSparks(@RequestParam symbol: Array<String>) = marketService.getSparks(symbol.toList())
}