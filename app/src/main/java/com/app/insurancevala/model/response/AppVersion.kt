package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

data class AppVersion (
    @SerializedName("Data")
    val Data: Setting? = null,
    @SerializedName("Details")
    val Details: Any?,
    @SerializedName("Message")
    val Message: String,
    @SerializedName("Status")
    val Status: Int
)
data class Setting (
    @SerializedName("Version")
    val Version: String
)