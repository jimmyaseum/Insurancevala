package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

data class NBLeadListModel(
    @SerializedName("ID")
    var ID: Int? = null,
    @SerializedName("LeadNo")
    var LeadNo: Int? = null,
    @SerializedName("NBLeadsGUID")
    var NBLeadsGUID: String? = null,
    @SerializedName("NBLeadBy")
    var NBLeadBy: Int? = null,
    @SerializedName("NBLeadByName")
    var NBLeadByName: String? = null,
    @SerializedName("InquiryTypeID")
    var InquiryTypeID: Int? = null,
    @SerializedName("InquirySubTypeID")
    var InquirySubTypeID: Int? = null,
    @SerializedName("LeadTypeID")
    var LeadTypeID: Int? = null,
    @SerializedName("LeadAllotmentID")
    var LeadAllotmentID: Int? = null,
    @SerializedName("LeadStatusID")
    var LeadStatusID: Int? = null,
    @SerializedName("ProposedAmount")
    var ProposedAmount: Double? = null,
    @SerializedName("Frequency")
    var Frequency: String? = null,
    @SerializedName("LeadDate")
    var LeadDate: String? = null,
    @SerializedName("LeadAllotmentName")
    var LeadAllotmentName: String? = null,
    @SerializedName("CoPersonAllotmentID")
    var CoPersonAllotmentID: Int? = null,
    @SerializedName("CoPersonAllotmentName")
    var CoPersonAllotmentName: String? = null,
    @SerializedName("CreatedBy")
    var CreatedBy: Int? = null,
    @SerializedName("CreatedByName")
    var CreatedByName: String? = null,
    @SerializedName("InquiryType")
    var InquiryType: String? = null,
    @SerializedName("InquirySubType")
    var InquirySubType: String? = null,
    @SerializedName("LeadType")
    var LeadType: String? = null,
    @SerializedName("LeadStatus")
    var LeadStatus: String? = null,
    @SerializedName("Details")
    var Details: String? = null,
    @SerializedName("Message")
    var Message: String? = null,
    @SerializedName("Status")
    var Status: Int? = null,
    @SerializedName("ErrorMessage")
    var ErrorMessage: String? = null
)

// --------------------------------------------
//NBInquiryByGUIDResponse

data class NBLeadByGUIDResponse(
    @SerializedName("Data")
    var Data: ArrayList<NBLeadListModel>? = null,
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

data class NBLeadListByGUIDResponse (
    @SerializedName("Data")
    var Data: NBLeadListModel? = null,
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

/*
// --------------------------------------------
//NBInquiryFindLeadStatusResponse

data class NBLeadLeadStatusResponse (
    @SerializedName("Data")
    var Data: ArrayList<NBInquiryModel>? = null,
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

*/
