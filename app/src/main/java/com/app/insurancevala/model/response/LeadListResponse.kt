package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

data class LeadListByGUIDResponse (
    @SerializedName("Data")
    var Data: LLModel? = null,
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

data class LLModel (
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
    val NBInquiryList: ArrayList<LeadListModel>? = null,
    @SerializedName("NBInquirySub")
    val NBInquirySub: LeadListModel? = null
)

data class LeadListModel (
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
    @SerializedName("InquiryDate")
    val InquiryDate: String? = null
)