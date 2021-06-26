package com.nodemules.stox.security

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.nodemules.stox.config.SecurityConfig
import io.vavr.control.Try
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream

@Configuration
class FirebaseConfig(
    val firebaseProperties: FirebaseProperties
): InitializingBean {

    @Bean
    fun firebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance(FirebaseApp.getInstance(SecurityConfig.FIREBASE_APP_NAME))

    override fun afterPropertiesSet() {
        Try.of {
            FirebaseApp.getInstance(SecurityConfig.FIREBASE_APP_NAME)
        }.onFailure {
            val serviceAccount = FileInputStream(firebaseProperties.configUri)

            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl(firebaseProperties.databaseUrl)
                .build()

            FirebaseApp.initializeApp(options, SecurityConfig.FIREBASE_APP_NAME)
        }
    }
}