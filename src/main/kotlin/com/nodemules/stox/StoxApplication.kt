package com.nodemules.stox

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients
@SpringBootApplication
class StoxApplication

fun main(args: Array<String>) {
	runApplication<StoxApplication>(*args)
}

