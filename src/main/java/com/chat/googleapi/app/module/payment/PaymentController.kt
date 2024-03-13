package com.chat.googleapi.app.module.payment

import com.google.gson.Gson

object PaymentController {

    fun handleRequest(data: String): String {
        val data = try {
            Gson().fromJson(data, RequestData::class.java)
        } catch (e: Throwable) {
            null
        }
        val token = data?.token
        val product = data?.product
        val result = ResponseData()
        if (token.isNullOrEmpty() || product.isNullOrEmpty()) {
            result.code = 5001
            result.msg = "params error, token or product is null or empty"
        } else {
            result.code = 200
            result.msg = "result: " + GooglePayVerifier.consumeProductPurchase(product, token)
        }
        return Gson().toJson(result)
    }
}