package com.chat.googleapi.app.module.login

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.gson.Gson

object LoginController {

    private const val SERVER_CLIENT_ID = "1038253465945-e5pi3qh12kevb2hnjdqe32esrg3884s2.apps.googleusercontent.com"

    fun handleRequest(data: String): String {
        val token = try {
            Gson().fromJson(data, RequestData::class.java).token
        } catch (e: Throwable) {
            null
        }
        val result = ResponseData()
        if (token.isNullOrEmpty()) {
            result.code = 5001
            result.msg = "params error, token is null or empty"
            result.userInfo = ""
        } else {
            result.code = 200
            result.msg = "success"
            result.userInfo = fetchUserInfo(token) ?: ""
        }
        return Gson().toJson(result)
    }

    private fun fetchUserInfo(idToken: String): UserInfo? {
        val googleIdToken = try {
            val verifier = GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance()
            )
                .setAudience(arrayListOf(SERVER_CLIENT_ID))
                .build()
            verifier.verify(idToken)
        } catch (e: Throwable) {
            return null
        }
        val payload = googleIdToken.payload
        val userInfo = UserInfo()
        userInfo.userId = payload.subject
        userInfo.email = payload.email
        userInfo.emailVerified = payload.emailVerified
        userInfo.name = payload["name"]?.toString() ?: ""
        userInfo.picture = payload["picture"]?.toString() ?: ""
        userInfo.locale = payload["locale"]?.toString() ?: ""
        return userInfo
    }
}