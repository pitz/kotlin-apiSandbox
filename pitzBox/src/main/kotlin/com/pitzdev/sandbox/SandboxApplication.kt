package com.pitzdev.sandbox

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SandboxApplication

fun main(args: Array<String>) {
	runApplication<SandboxApplication>(*args)
}
