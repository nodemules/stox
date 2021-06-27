package com.nodemules.stox.failure

data class GenericFailure(val message: String?): Failure {
    override val code = 500

    override val reason = message
}