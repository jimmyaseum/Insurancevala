package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class CallTypeResponse {
    @SerializedName("Data")
    var Data: ArrayList<CallTypeModel>? = null
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

data class CallTypeModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("CallTypeGUID")
    val CallTypeGUID: String? = null,
    @SerializedName("CallType")
    val CallType: String? = null,
    @SerializedName("IsActive")
    val IsActive: Boolean? = null,
    @SerializedName("IsSelected")
    var IsSelected : Boolean? = false
)
