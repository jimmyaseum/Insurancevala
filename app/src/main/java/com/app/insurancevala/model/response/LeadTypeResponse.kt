package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class LeadTypeResponse {
    @SerializedName("Data")
    var Data: ArrayList<LeadTypeModel>? = null
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

data class LeadTypeModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("LeadTypeGUID")
    val LeadTypeGUID: String? = null,
    @SerializedName("LeadType")
    val LeadType: String? = null,
    @SerializedName("IsActive")
    val IsActive: Boolean? = null,
    @SerializedName("IsSelected")
    var IsSelected : Boolean? = false
)