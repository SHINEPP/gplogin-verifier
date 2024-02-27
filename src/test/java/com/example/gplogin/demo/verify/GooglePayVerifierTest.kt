package com.example.gplogin.demo.verify

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class GooglePayVerifierTest {

    @Test
    fun getProductPurchase() {
        GooglePayVerifier.getProductPurchase("com.period.tracker.health.reminder.women.v1.10coins",
            "naipcpepaimaodmchilbdklp.AO-J1OyHVw3IXA7mD74ysPuALx3W_S_znNibWZ9k8t5-4CsQ7bD-o0ay16jMdIf_F4zAdQStewsUQ2gyRGOsS1cjl76Mh6wHiKAwjhr8JubEYspG-IEwhYdx2r20GEQrkZDWWAmzhFqK")
    }
}