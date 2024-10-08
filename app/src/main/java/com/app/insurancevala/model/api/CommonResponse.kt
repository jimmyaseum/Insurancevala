package com.app.insurancevala.model.api

import com.google.gson.annotations.SerializedName

class CommonResponse {
    @SerializedName("Data")
    var Data: String? = null
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
class LeadCountResponse {
    @SerializedName("Data")
    var Data: LeadModel? = null
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
data class LeadModel (
    @SerializedName("LeadCount")
    var LeadCount: Int? = null
    )