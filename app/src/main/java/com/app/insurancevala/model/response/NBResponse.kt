package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class NBResponse {
    @SerializedName("Data")
    var Data: ArrayList<NewNBModel>? = null
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
data class NewNBModel (
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
    val UpdatedOn: String? = null
)
data class NBModel (
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
    val NBInquiryList: ArrayList<NBInquiryModel>? = null,
    @SerializedName("NBInquirySub")
    val NBInquirySub: NBInquiryModel? = null
)
data class NBInquiryModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("NBInquiryTypeGUID")
    val NBInquiryTypeGUID: String? = null,
    @SerializedName("InquiryNo")
    val InquiryNo: Int? = null,
    @SerializedName("NBInquiryID")
    val NBInquiryID: Int? = null,
    @SerializedName("NBInquiryBy")
    val NBInquiryBy: Int? = null,
    @SerializedName("NBInquiryByName")
    val NBInquiryByName: String? = null,
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
    @SerializedName("InquiryAllotmentID")
    val InquiryAllotmentID: Int? = null,
    @SerializedName("InquiryAllotmentName")
    val InquiryAllotmentName: String? = null,
    @SerializedName("CoPersonAllotmentID")
    val CoPersonAllotmentID: Int? = null,
    @SerializedName("CoPersonAllotmentName")
    val CoPersonAllotmentName: String? = null,
    @SerializedName("LeadAllotmentID")
    var LeadAllotmentID: Int? = null,
    @SerializedName("LeadAllotmentName")
    var LeadAllotmentName: String? = null,
    @SerializedName("CreatedBy")
    val CreatedBy: Int? = null,
    @SerializedName("CreatedByName")
    val CreatedByName: String? = null,
    @SerializedName("LeadStatusID")
    val LeadStatusID: Int? = null,
    @SerializedName("LeadStatus")
    val LeadStatus: String? = null,
    @SerializedName("ProposedAmount")
    val ProposedAmount: String? = null,
    @SerializedName("ClosingAmount")
    val ClosingAmount: String? = null,
    @SerializedName("Frequency")
    val Frequency: String? = null,
    @SerializedName("InquiryDate")
    val InquiryDate: String? = null
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

// --------------------------------------------
//NBInquiryFindLeadStatusResponse

data class NBInquiryLeadStatusResponse (
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

