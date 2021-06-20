package com.nodemules.stox.config

import com.nodemules.stox.security.SecurityFilter
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(
    val securityFilter: SecurityFilter
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        super.configure(http)
        http?.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter::class.java)
    }
}