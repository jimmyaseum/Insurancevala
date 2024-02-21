package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class CallPurposeResponse {
    @SerializedName("Data")
    var Data: ArrayList<CallPurposeModel>? = null
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

data class CallPurposeModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("CallPurposeGUID")
    val CallPurposeGUID: String? = null,
    @SerializedName("CallPurpose")
    val CallPurpose: String? = null,
    @SerializedName("IsActive")
    val IsActive: Boolean? = null,
    @SerializedName("IsSelected")
    var IsSelected : Boolean? = false
)
