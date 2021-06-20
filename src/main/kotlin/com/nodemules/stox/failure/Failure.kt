package com.nodemules.stox.failure

interface Failure {

    fun getCode(): Int

    fun getReason(): String?
}