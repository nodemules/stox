package com.nodemules.stox.extensions

import com.nodemules.stox.failure.Failure
import io.vavr.control.Either

fun <T> T.toEither(): Either<Failure, T> = Either.right(this)
