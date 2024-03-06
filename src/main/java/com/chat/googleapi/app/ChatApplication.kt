package com.chat.googleapi.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("com.chat.googleapi.app.router")
class ChatApplication

fun main(args: Array<String>) {
	runApplication<ChatApplication>(*args)
}
