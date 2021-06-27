package com.nodemules.stox.failure

import org.springframework.http.HttpStatus

data class HttpFailure(
    private val httpStatus: HttpStatus,
    private val message: String?
) : Failure {
    override val code = httpStatus.value()
    override val reason = message
}