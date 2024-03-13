package com.chat.googleapi.app.router

import com.chat.googleapi.app.module.login.LoginController
import com.chat.googleapi.app.module.payment.PaymentController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class GoogleRouter {

    @PostMapping(path = ["google/login"], produces = ["application/json; charset=UTF-8"])
    @ResponseBody
    fun login(@RequestBody data: String): String {
        return LoginController.handleRequest(data)
    }

    @PostMapping(path = ["google/payment"], produces = ["application/json; charset=UTF-8"])
    @ResponseBody
    fun payment(@RequestBody data: String): String {
        return PaymentController.handleRequest(data)
    }
}