package com.chat.googleapi.app.module.login

import com.google.gson.annotations.SerializedName

class ResponseData {
    @SerializedName("code")
    var code = 0

    @SerializedName("msg")
    var msg = ""

    @SerializedName("data")
    var userInfo: Any? = null
}

class UserInfo {
    @SerializedName("userId")
    var userId = ""

    @SerializedName("email")
    var email = ""

    @SerializedName("emailVerified")
    var emailVerified = false

    @SerializedName("name")
    var name = ""

    @SerializedName("picture")
    var picture = ""

    @SerializedName("locale")
    var locale = ""
}