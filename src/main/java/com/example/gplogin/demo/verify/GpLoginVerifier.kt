package com.example.gplogin.demo.verify

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory


object GpLoginVerifier {

    private const val SERVER_CLIENT_ID = "1038253465945-e5pi3qh12kevb2hnjdqe32esrg3884s2.apps.googleusercontent.com"

    fun verify(idToken: String) {
        val verifier = GoogleIdTokenVerifier.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance()
        )
            .setAudience(arrayListOf(SERVER_CLIENT_ID))
            .build()

        verifier.verify(idToken)?.let { token ->
            val payload = token.payload
            val userId = payload.subject
            val email = payload.email
            val emailVerified = payload.emailVerified
            println("userId = $userId")
            println("email = $email")
            println("emailVerified = $emailVerified")
            for (key in payload.keys) {
                println("$key =  ${payload[key]}")
            }
        }
    }
}