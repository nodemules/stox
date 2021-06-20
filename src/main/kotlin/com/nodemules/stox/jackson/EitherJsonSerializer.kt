package com.nodemules.stox.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.nodemules.stox.failure.Failure
import io.vavr.control.Either
import org.springframework.boot.jackson.JsonComponent
import java.io.IOException

@JsonComponent
class EitherJsonSerializer : JsonSerializer<Either<Failure, *>>() {

    @Throws(IOException::class)
    override fun serialize(value: Either<Failure, *>?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        value
            ?.peek { gen?.writeObject(it) }
            ?.peekLeft { gen?.writeObject(it) }
    }
}

