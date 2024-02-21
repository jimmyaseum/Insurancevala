package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class InitialResponse {
    @SerializedName("Data")
    var Data: ArrayList<InitialModel>? = null
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

data class InitialModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("InitialGUID")
    val InitialGUID: String? = null,
    @SerializedName("Initial")
    val Initial: String? = null,
    @SerializedName("IsActive")
    val IsActive: Boolean? = null,
    @SerializedName("IsSelected")
    var IsSelected : Boolean? = false
)
