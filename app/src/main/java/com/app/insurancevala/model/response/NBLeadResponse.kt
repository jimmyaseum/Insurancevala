package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class NBLeadResponse {
    @SerializedName("Data")
    var Data: ArrayList<NBLeadModel>? = null

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

data class NBLeadModel(
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("NBInquiryGUID")
    val NBInquiryGUID: String? = null,
    @SerializedName("LeadID")
    val LeadID: Int? = null,
    @SerializedName("InquiryNo")
    val InquiryNo: Int? = null,
    @SerializedName("LeadName")
    val LeadName: String? = null,
    @SerializedName("LeadGUID")
    val LeadGUID: String? = null,
    @SerializedName("MobileNo")
    val MobileNo: String? = null,
    @SerializedName("LeadStageID")
    val LeadStageID: Int? = null,
    @SerializedName("AllotmentID")
    val AllotmentID: Int? = null,
    @SerializedName("AllotmentName")
    val AllotmentName: String? = null,
    @SerializedName("FamilyID")
    val FamilyID: Int? = null,
    @SerializedName("FamilyMemberName")
    val FamilyMemberName: String? = null,
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
    val NBInquiryList: ArrayList<NBLeadListModel>? = null,
    @SerializedName("NBInquirySub")
    val NBInquirySub: NBLeadListModel? = null
)

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
