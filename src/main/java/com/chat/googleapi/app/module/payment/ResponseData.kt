package com.chat.googleapi.app.module.payment

import com.google.gson.annotations.SerializedName

class ResponseData {
    @SerializedName("code")
    var code = 0

    @SerializedName("msg")
    var msg = ""
}