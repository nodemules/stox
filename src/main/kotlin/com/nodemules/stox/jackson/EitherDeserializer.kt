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
import io.vavr.control.Try
import mu.KLogging
import org.springframework.boot.jackson.JsonComponent
import org.springframework.boot.jackson.JsonObjectDeserializer

@JsonComponent
class EitherDeserializer(
    private val type: JavaType? = null
) : JsonObjectDeserializer<Either<Failure, *>>(), ContextualDeserializer {

    @Throws(JsonProcessingException::class)
    override fun deserializeObject(
        jsonParser: JsonParser?,
        context: DeserializationContext?,
        codec: ObjectCodec?,
        tree: JsonNode?
    ): Either<Failure, *> {
        return type
            ?.let {
                Try.of {
                    codec?.readValue<Any>(codec.treeAsTokens(tree), it).run { Either.right<Failure, Any>(this) }
                }.onFailure { ex ->
                    logger.error(ex) { "Unable to deserialize response for $type" }
                    Either.left<Failure, Any>(GenericFailure(ex.message))
                }.orNull
            }
            ?: Either.left<Failure, Any>(GenericFailure("Unable to deserialize response for $type"))
    }

    override fun createContextual(ctxt: DeserializationContext?, property: BeanProperty?): JsonDeserializer<Either<Failure, *>> {
        return EitherDeserializer(ctxt?.contextualType?.containedTypeOrUnknown(1)
            .takeUnless { it == TypeFactory.unknownType() })
    }

    companion object : KLogging()
}