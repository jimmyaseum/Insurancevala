package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class CallResultResponse {
    @SerializedName("Data")
    var Data: ArrayList<CallResultModel>? = null
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

data class CallResultModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("CallResultGUID")
    val CallResultGUID: String? = null,
    @SerializedName("CallResult")
    val CallResult: String? = null,
    @SerializedName("IsActive")
    val IsActive: Boolean? = null,
    @SerializedName("IsSelected")
    var IsSelected : Boolean? = false
)
