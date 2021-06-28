package com.nodemules.stox.market

import com.nodemules.stox.caching.DummyCache
import com.nodemules.stox.failure.Failure
import com.nodemules.stox.integrations.yahoo.Spark
import io.vavr.control.Either
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Component

@Component
class SparkDummyCache : DummyCache<Spark> {

    val dummy = object : DummyCache<Spark> {}

    @Caching(
        put = [CachePut(value = ["sparkCache"], key = "#value.symbol")]
    )
    override fun put(value: Spark): Either<Failure, Spark> = dummy.put(value)

    @Cacheable("sparkCache", key = "#key")
    override fun get(key: String): Either<Failure, Spark> = dummy.get(key)
}