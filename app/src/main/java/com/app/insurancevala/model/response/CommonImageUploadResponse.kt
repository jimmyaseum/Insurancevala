package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class CommonImageUploadResponse {
    @SerializedName("Data")
    var Data: CommonImageUploadModel? = null

    @SerializedName("Details")
    var Details: String? = null

    @SerializedName("Message")
    var Message: String? = null

    @SerializedName("Status")
    var Status: Int? = null

    @SerializedName("ItemCount")
    var ItemCount: Int? = null

    @SerializedName("ErrorMessage")
    var ErrorMessage: String? = null
}

data class CommonImageUploadModel(
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("MainImage")
    val MainImage: String? = null,
    @SerializedName("OtherImage")
    val OtherImage: String? = null,
    @SerializedName("Data")
    val Data: String? = null,
    @SerializedName("Details")
    val Details: String? = null,
    @SerializedName("Message")
    val Message: Boolean? = null,
    @SerializedName("Status")
    var Status: Int? = null,
    @SerializedName("ItemCount")
    var ItemCount: Int? = null,
    @SerializedName("ErrorMessage")
    var ErrorMessage: String? = null
)