package com.nodemules.stox.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("firebase")
data class FirebaseProperties(
    val configUri: String,
    val databaseUrl: String
)