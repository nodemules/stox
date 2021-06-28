package com.nodemules.stox.config

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.nodemules.stox.failure.Failure
import com.nodemules.stox.integrations.yahoo.Spark
import com.nodemules.stox.quote.GlobalQuote
import io.vavr.control.Either
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer
import org.springframework.data.redis.serializer.RedisSerializer
import java.time.Duration

@EnableCaching
@Configuration
@ConditionalOnProperty(value = ["cache.enabled"], havingValue = "true")
class CacheConfig(
    val objectMapper: ObjectMapper,
    val cacheProperties: CacheProperties
) {

    @Bean
    fun cacheConfiguration() = RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofSeconds(cacheProperties.ttl))
        .disableCachingNullValues()

    @Bean
    fun redisCacheManagerBuilderCustomizer() = RedisCacheManagerBuilderCustomizer { builder ->
        cacheConfigurationFactory().forEach {
            builder.withCacheConfiguration(it.key, cacheConfiguration().serializeValuesWith(fromSerializer(it.value)))
        }
    }

    private fun cacheConfigurationFactory(): Map<String, EitherRedisSerializer<*>> = mapOf(
        "quoteCache" to object : EitherRedisSerializer<GlobalQuote>(objectMapper) {
            override fun typeReference(): TypeReference<GlobalQuote> = object : TypeReference<GlobalQuote>() {
            }
        },
        "trendingCache" to object : EitherRedisSerializer<List<GlobalQuote>>(objectMapper) {
            override fun typeReference(): TypeReference<List<GlobalQuote>> = object : TypeReference<List<GlobalQuote>>() {
            }
        },
        "sparkCache" to object : EitherRedisSerializer<Spark>(objectMapper) {
            override fun typeReference(): TypeReference<Spark> = object : TypeReference<Spark>() {
            }
        }
    )

    abstract class EitherRedisSerializer<T>(
        private val objectMapper: ObjectMapper
    ) : RedisSerializer<Either<Failure, T>> {

        abstract fun typeReference(): TypeReference<T>

        override fun serialize(value: Either<Failure, T>?): ByteArray? =
            value?.map { objectMapper.writeValueAsBytes(it) }?.getOrElse(EMPTY_ARRAY)

        override fun deserialize(bytes: ByteArray?): Either<Failure, T>? =
            bytes?.takeIf { it.isNotEmpty() }?.let { objectMapper.readValue(bytes, typeReference()) }?.let { Either.right(it) }

        companion object {
            val EMPTY_ARRAY = byteArrayOf()

        }
    }
}