package com.chat.googleapi.app.router

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeRouter {
    @GetMapping(path = ["/"])
    @ResponseBody
    fun home(): String {
        return "Hello World!"
    }
}