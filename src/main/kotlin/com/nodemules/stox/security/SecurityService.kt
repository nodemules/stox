package com.nodemules.stox.security

import com.google.auth.Credentials
import org.springframework.security.core.userdetails.User

interface SecurityService {

    fun getUser(): User

    fun getCredentials(): Credentials

    fun isPublic(): Boolean

    fun getAuthorization(): String?
}