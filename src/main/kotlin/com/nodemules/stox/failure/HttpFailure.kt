package com.nodemules.stox.failure

import org.springframework.http.HttpStatus

data class HttpFailure(
    private val httpStatus: HttpStatus,
    private val message: String?
): Failure {
    override fun getCode(): Int = httpStatus.value()
    override fun getReason(): String? = message
}