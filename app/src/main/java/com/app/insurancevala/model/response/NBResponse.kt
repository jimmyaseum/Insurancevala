package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class NBResponse {
    @SerializedName("Data")
    var Data: ArrayList<NBModel>? = null
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
data class NBModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("NBInquiryGUID")
    val NBInquiryGUID: String? = null,
    @SerializedName("LeadID")
    val LeadID: Int? = null,
    @SerializedName("LeadName")
    val LeadName: String? = null,
    @SerializedName("MobileNo")
    val MobileNo: String? = null,
    @SerializedName("LeadStageID")
    val LeadStageID: Int? = null,
    @SerializedName("AllotmentID")
    val AllotmentID: Int? = null,
    @SerializedName("AllotmentName")
    val AllotmentName: String? = null,
    @SerializedName("IsActive")
    val IsActive: Boolean? = null,
    @SerializedName("CreatedBy")
    val CreatedBy: Int? = null,
    @SerializedName("UpdatedBy")
    val UpdatedBy: Int? = null,
    @SerializedName("CreatedOn")
    val CreatedOn: String? = null,
    @SerializedName("UpdatedOn")
    val UpdatedOn: String? = null,
    @SerializedName("NBInquiryList")
    val NBInquiryList: ArrayList<NBInquiryModel>? = null,
    @SerializedName("NBInquirySUb")
    val NBInquirySUb: NBInquiryModel? = null
)
data class NBInquiryModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("NBInquiryTypeGUID")
    val NBInquiryTypeGUID: String? = null,
    @SerializedName("NBInquiryID")
    val NBInquiryID: Int? = null,
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
    @SerializedName("InquiryAllotmentID")
    val InquiryAllotmentID: Int? = null,
    @SerializedName("InquiryAllotmentName")
    val InquiryAllotmentName: String? = null,
    @SerializedName("LeadStatusID")
    val LeadStatusID: Int? = null,
    @SerializedName("LeadStatus")
    val LeadStatus: String? = null,
    @SerializedName("ProposedAmount")
    val ProposedAmount: Double? = null,
    @SerializedName("Frequency")
    val Frequency: String? = null,
)

// --------------------------------------------
//NBInquiryByGUIDResponse

data class NBInquiryByGUIDResponse (
    @SerializedName("Data")
    var Data: NBModel? = null,
    @SerializedName("Details")
    var Details: String? = null,
    @SerializedName("Message")
    var Message: String? = null,
    @SerializedName("Status")
    var Status: Int? = null,
    @SerializedName("ItemCount")
    var ItemCount: Int? = null,
    @SerializedName("ErrorMessage")
    var ErrorMessage: String? = null
    )

data class NBInquiryTypeByGUIDResponse (
    @SerializedName("Data")
    var Data: NBInquiryModel? = null,
    @SerializedName("Details")
    var Details: String? = null,
    @SerializedName("Message")
    var Message: String? = null,
    @SerializedName("Status")
    var Status: Int? = null,
    @SerializedName("ItemCount")
    var ItemCount: Int? = null,
    @SerializedName("ErrorMessage")
    var ErrorMessage: String? = null
    )
data class NBInquiryTypeAddUpdateResponse (
    @SerializedName("Data")
    var Data: String? = null,
    @SerializedName("Details")
    var Details: String? = null,
    @SerializedName("Message")
    var Message: String? = null,
    @SerializedName("Status")
    var Status: Int? = null,
    @SerializedName("ItemCount")
    var ItemCount: Int? = null,
    @SerializedName("ErrorMessage")
    var ErrorMessage: String? = null
)

