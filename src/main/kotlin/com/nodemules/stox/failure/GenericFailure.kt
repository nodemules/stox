package com.nodemules.stox.failure

data class GenericFailure(val message: String?): Failure {
    override fun getCode(): Int = 500

    override fun getReason(): String? = message
}