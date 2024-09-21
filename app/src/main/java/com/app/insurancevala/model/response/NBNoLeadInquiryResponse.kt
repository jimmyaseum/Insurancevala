package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class NBNoLeadInquiryResponse {
    @SerializedName("Data")
    var Data: ArrayList<NBNoLeadInquiryModel>? = null

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

data class NBNoLeadInquiryModel(
    @SerializedName("Source")
    var Source: String? = null,
    @SerializedName("ID")
    var ID: Int? = null,
    @SerializedName("NBLeadsGUID")
    var NBLeadsGUID: String? = null,
    @SerializedName("NBInquiryTypeGUID")
    val NBInquiryTypeGUID: String? = null,
    @SerializedName("LeadID")
    val LeadID: Int? = null,
    @SerializedName("InquiryNo")
    var InquiryNo: Int? = null,
    @SerializedName("InquiryDate")
    var InquiryDate: String? = null,
    @SerializedName("InquiryType")
    var InquiryType: String? = null,
    @SerializedName("InquiryPerson")
    var InquiryPerson: String? = null,
    @SerializedName("AllotmentName")
    var AllotmentName: String? = null,
    @SerializedName("CoPersonAllotmentName")
    var CoPersonAllotmentName: String? = null,
    @SerializedName("ProposedAmount")
    var ProposedAmount: String? = null,
    @SerializedName("InquiryTypeID")
    var InquiryTypeID: String? = null,
    @SerializedName("ClosingAmount")
    var ClosingAmount: String? = null,
    @SerializedName("InquirySubType")
    var InquirySubType: String? = null,
    @SerializedName("LeadType")
    var LeadType: String? = null,
    @SerializedName("LeadStatus")
    var LeadStatus: String? = null,
    @SerializedName("Frequency")
    var Frequency: String? = null,
    @SerializedName("CreatedOn")
    var CreatedOn: String? = null,
    @SerializedName("CreatedBy")
    var CreatedBy: String? = null,
    @SerializedName("CreatedByName")
    var CreatedByName: String? = null,
    @SerializedName("Details")
    var Details: String? = null,
    @SerializedName("Message")
    var Message: String? = null,
    @SerializedName("Status")
    var Status: String? = null,
    @SerializedName("ErrorMessage")
    var ErrorMessage: String? = null
)
