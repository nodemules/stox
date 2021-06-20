package com.nodemules.stox.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.type.TypeFactory
import com.nodemules.stox.failure.Failure
import com.nodemules.stox.failure.GenericFailure
import io.vavr.control.Either
import org.springframework.boot.jackson.JsonComponent
import org.springframework.boot.jackson.JsonObjectDeserializer

@JsonComponent
class EitherDeserializer : JsonObjectDeserializer<Either<Failure, *>>(), ContextualDeserializer {

    private var type: JavaType? = null

    @Throws(JsonProcessingException::class)
    override fun deserializeObject(
        jsonParser: JsonParser?,
        context: DeserializationContext?,
        codec: ObjectCodec?,
        tree: JsonNode?
    ): Either<Failure, *> {
        return type
            ?.let { codec?.readValue<Any>(codec.treeAsTokens(tree), it).run { Either.right(this) } }
            ?: Either.left<Failure, Any>(GenericFailure("Unable to deserialize response for $type"))
    }

    override fun createContextual(ctxt: DeserializationContext?, property: BeanProperty?): JsonDeserializer<Either<Failure, *>> {
        type = ctxt?.contextualType?.containedTypeOrUnknown(1)
            .takeUnless { it == TypeFactory.unknownType() }
        return this
    }
}