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
    @SerializedName("InquiryNo")
    val InquiryNo: Int? = null,
    @SerializedName("NBInquiryTypeGUID")
    val NBInquiryTypeGUID: String? = null,
    @SerializedName("LeadID")
    val LeadID: Int? = null,
    @SerializedName("NBInquiryID")
    val NBInquiryID: Int? = null,
    @SerializedName("LeadGUID")
    val LeadGUID: String? = null,
    @SerializedName("CreatedByName")
    val CreatedByName: String? = null,
    @SerializedName("CoPersonAllotmentID")
    val CoPersonAllotmentID: Int? = null,
    @SerializedName("CoPersonAllotmentName")
    val CoPersonAllotmentName: String? = null,
    @SerializedName("InquiryAllotmentID")
    val InquiryAllotmentID: Int? = null,
    @SerializedName("InquiryAllotmentName")
    val InquiryAllotmentName: String? = null,
    @SerializedName("InquiryTypeID")
    val InquiryTypeID: String? = null,
    @SerializedName("InquiryType")
    val InquiryType: String? = null,
    @SerializedName("InquirySubTypeID")
    val InquirySubTypeID: String? = null,
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
    val ProposedAmount: String? = null,
    @SerializedName("Frequency")
    val Frequency: String? = null,
    @SerializedName("LeadName")
    val LeadName: String? = null,
    @SerializedName("InquiryDate")
    val InquiryDate: String? = null,
    @SerializedName("GroupCode")
    val GroupCode: String? = null,
    @SerializedName("ClosingAmount")
    val ClosingAmount: String? = null
    )
