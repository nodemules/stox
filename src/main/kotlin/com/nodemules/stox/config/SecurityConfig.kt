package com.nodemules.stox.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.nodemules.stox.security.SecurityFilter
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import java.time.ZonedDateTime
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
class SecurityConfig(
    val objectMapper: ObjectMapper,
    val securityFilter: SecurityFilter
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
            .cors { it.configurationSource { CorsConfiguration().applyPermitDefaultValues() } }
            .httpBasic().disable().exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint())
            .and().authorizeRequests().anyRequest().authenticated()
            .and().addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter::class.java)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }

    private fun restAuthenticationEntryPoint(): AuthenticationEntryPoint =
        AuthenticationEntryPoint { _: HttpServletRequest?, httpServletResponse: HttpServletResponse, _: AuthenticationException? ->
            val errorCode = 401
            val errorObject: MutableMap<String, Any> = hashMapOf(
                "message" to "Unauthorized access of protected resource, invalid credentials",
                "error" to HttpStatus.UNAUTHORIZED,
                "code" to errorCode,
                "timestamp" to ZonedDateTime.now().toInstant().epochSecond
            )
            httpServletResponse.contentType = "application/json;charset=UTF-8"
            httpServletResponse.status = errorCode
            httpServletResponse.writer.write(objectMapper.writeValueAsString(errorObject))
        }

    companion object {
        const val FIREBASE_APP_NAME = "stox-api"
    }
}