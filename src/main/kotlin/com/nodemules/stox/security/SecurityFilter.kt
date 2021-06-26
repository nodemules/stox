package com.nodemules.stox.security

import com.google.firebase.auth.FirebaseAuth
import io.vavr.control.Try
import mu.KLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class SecurityFilter(
    val securityService: SecurityService
) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        Try.of {
            securityService.getAuthorization()
                ?.let {
                    FirebaseAuth.getInstance().verifyIdToken(it)
                        ?.let { idToken -> Credentials(idToken) }
                        ?.let { credentials ->
                            UsernamePasswordAuthenticationToken(
                                FirebaseAuth.getInstance().getUser(credentials.token.uid),
                                credentials,
                                listOf()
                            )
                        }
                }
                ?.also {
                    it.details = WebAuthenticationDetailsSource().buildDetails(request)
                }
                ?.let { authentication ->
                    SecurityContextHolder.getContext().authentication = authentication
                }
        }.onFailure {
            logger.error("An error occurred authenticating with Firebase", it.cause)
        }
        filterChain.doFilter(request, response)
    }

    companion object : KLogging()
}