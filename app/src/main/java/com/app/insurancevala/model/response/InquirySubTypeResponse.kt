package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class InquirySubTypeResponse {

    @SerializedName("Data")
    var Data: ArrayList<InquirySubTypeModel>? = null
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

data class InquirySubTypeModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("InquirySubTypeGUID")
    val InquirySubTypeGUID: String? = null,
    @SerializedName("InquirySubType")
    val InquirySubType: String? = null,
    @SerializedName("InquiryTypeID")
    val InquiryTypeID: Int? = null,
    @SerializedName("InquiryType")
    val InquiryType: String? = null,
    @SerializedName("IsActive")
    val IsActive: Boolean? = null,
    @SerializedName("IsSelected")
    var IsSelected : Boolean? = false
)