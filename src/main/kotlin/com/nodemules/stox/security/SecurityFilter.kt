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
            request.cookies
                ?.first { "session" == it.name }
                ?.let { FirebaseAuth.getInstance().verifySessionCookie(it.value, true) }
                ?.let { token ->
                    securityService.getAuthorization()
                        ?.let { FirebaseAuth.getInstance().verifyIdToken(securityService.getAuthorization()) }
                        ?.let { idToken -> Credentials(token, idToken) }
                        ?.takeIf { it.isValid() }
                        ?.let { credentials ->
                            UsernamePasswordAuthenticationToken(
                                FirebaseAuth.getInstance().getUser(token.uid),
                                credentials
                            )
                        }
                }
                ?.also {
                    it.details = WebAuthenticationDetailsSource().buildDetails(request)
                }
                ?.let { authentication ->
                    SecurityContextHolder.getContext().authentication = authentication
                }
        }.runCatching {
            logger.error("An error occurred authenticating with Firebase", this.cause)
        }
    }

    companion object : KLogging()
}