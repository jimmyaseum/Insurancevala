package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class InquiryTypeResponse {

    @SerializedName("Data")
    var Data: ArrayList<InquiryTypeModel>? = null
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

data class InquiryTypeModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("InquiryTypeGUID")
    val InquiryTypeGUID: String? = null,
    @SerializedName("InquiryType")
    val InquiryType: String? = null,
    @SerializedName("IsActive")
    val IsActive: Boolean? = null,
    @SerializedName("IsSelected")
    var IsSelected : Boolean? = false
)
