package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class LeadImageResponse {
    @SerializedName("Data")
    var Data: LeadImageModel? = null
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
data class LeadImageModel (
    @SerializedName("LeadImage")
    var LeadImage: String? = null
        )