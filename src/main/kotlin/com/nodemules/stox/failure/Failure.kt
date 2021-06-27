package com.nodemules.stox.failure

interface Failure {
    val code: Int
    val reason: String?
}