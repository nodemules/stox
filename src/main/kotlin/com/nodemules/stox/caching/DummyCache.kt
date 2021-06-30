package com.nodemules.stox.caching

import com.nodemules.stox.failure.Failure
import com.nodemules.stox.failure.GenericFailure
import io.vavr.control.Either

interface DummyCache<T> {

    fun put(value: T): Either<Failure, T> = Either.right(value)

    fun get(key: String): Either<Failure, T> = Either.left(GenericFailure("Operation not supported"))
}
