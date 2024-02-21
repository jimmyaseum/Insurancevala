package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class DashboardInnerResponse {
    @SerializedName("Data")
    var Data: ArrayList<DashboardInnerModel>? = null
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
data class DashboardInnerModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("NBInquiryTypeGUID")
    val NBInquiryTypeGUID: String? = null,
    @SerializedName("NBInquiryID")
    val NBInquiryID: Int? = null,
    @SerializedName("InquiryAllotmentName")
    val InquiryAllotmentName: String? = null,
    @SerializedName("InquiryTypeID")
    val InquiryTypeID: Int? = null,
    @SerializedName("InquiryType")
    val InquiryType: String? = null,
    @SerializedName("InquirySubTypeID")
    val InquirySubTypeID: Int? = null,
    @SerializedName("InquirySubType")
    val InquirySubType: String? = null,
    @SerializedName("LeadTypeID")
    val LeadTypeID: Int? = null,
    @SerializedName("LeadType")
    val LeadType: String? = null,
    @SerializedName("LeadStatusID")
    val LeadStatusID: Int? = null,
    @SerializedName("LeadStatus")
    val LeadStatus: String? = null,
    @SerializedName("ProposedAmount")
    val ProposedAmount: Double? = null,
    @SerializedName("Frequency")
    val Frequency: String? = null,
    @SerializedName("LeadName")
    val LeadName: String? = null
    )
