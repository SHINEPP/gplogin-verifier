package com.example.gplogin.demo.router

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class VerifyRouter {

    @GetMapping(path = ["verify"], produces = ["application/json; charset=UTF-8"])
    @ResponseBody
    fun verify(): String {
        println("VerifyRouter::verify()")
        return "Verify"
    }
}