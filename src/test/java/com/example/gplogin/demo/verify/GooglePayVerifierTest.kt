package com.example.gplogin.demo.verify

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class GooglePayVerifierTest {

    @Test
    fun getProductPurchase() {
        GooglePayVerifier.consumeProductPurchase("com.period.tracker.health.reminder.women.v1.10coins",
            "kjegdaaaaadgddadfbkpijil.AO-J1OwC0DhNEIcgDDD9wa8Mi9kkdRWRHbGgdlTRwX8Seut0BafIFChfYW1TeHnHkQLPCze2m2Fm0QRgz2xt2gye1jMnrBrETcljVAq-04UWXBb4b_SoPCZRxu0HKh4hL-Ew4vDEVZPk")
    }
}