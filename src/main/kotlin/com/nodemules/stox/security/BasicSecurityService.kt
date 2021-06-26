package com.nodemules.stox.security

import com.google.auth.Credentials
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class BasicSecurityService(
    val httpServletRequest: HttpServletRequest
) : SecurityService {

    override fun getUser(): User = SecurityContextHolder.getContext()
        ?.authentication
        ?.principal
        .takeIf { it is User }
        .run { this as User }

    override fun getCredentials(): Credentials = SecurityContextHolder.getContext()
        ?.authentication
        ?.credentials
        .takeIf { it is Credentials }
        .run { this as Credentials }

    override fun isPublic(): Boolean = listOf("/swagger-ui.html").contains(httpServletRequest.requestURI)

    override fun getAuthorization(): String? = httpServletRequest.getHeaders(HEADER_AUTHORIZATION).toList()
        .firstOrNull { it.startsWith(PREFIX_BEARER) }
        ?.run { this.substring(PREFIX_BEARER.length) }

    companion object {
        private val HEADER_AUTHORIZATION = "Authorization"
        private val PREFIX_BEARER = "Bearer "
    }
}