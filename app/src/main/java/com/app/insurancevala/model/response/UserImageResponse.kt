package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class UserImageResponse {
    @SerializedName("Data")
    var Data: UserImageModel? = null
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
data class UserImageModel (
    @SerializedName("UserImage")
    var UserImage: String? = null
        )