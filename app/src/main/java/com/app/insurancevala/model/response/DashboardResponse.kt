package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class DashboardResponse {
    @SerializedName("Data")
    var Data: DashboardModel? = null
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
data class DashboardModel (
    @SerializedName("TotalInquiry")
    val TotalInquiry: Int? = null,
    @SerializedName("OpenNBInquiry")
    val OpenNBInquiry: Int? = null,
    @SerializedName("OwnInquiry")
    val OwnInquiry: Int? = null,
    @SerializedName("InquiryTypeWiseCounts")
    var InquiryTypeWiseCounts: ArrayList<InquiryTypeWiseCountsModel>? = null,
    @SerializedName("LeadStatusWiseCount")
    var LeadStatusWiseCount: ArrayList<LeadStatusWiseCountModel>? = null,
    @SerializedName("EmployeeWiseCounts")
    var EmployeeWiseCounts: ArrayList<EmployeeWiseCountsModel>? = null
)
data class InquiryTypeWiseCountsModel (
    @SerializedName("InquiryType")
    val InquiryType: String? = null,
    @SerializedName("InquiryTypeID")
    val InquiryTypeID: Int? = null,
    @SerializedName("InquiryTypeCount")
    val InquiryTypeCount: Int? = null,
    @SerializedName("ProposedAmount")
    val ProposedAmount: Double? = null,
)
data class LeadStatusWiseCountModel (
    @SerializedName("LeadStatus")
    val LeadStatus: String? = null,
    @SerializedName("LeadStatusID")
    val LeadStatusID: Int? = null,
    @SerializedName("LeadStatusCount")
    val LeadStatusCount: Int? = null,
)
data class EmployeeWiseCountsModel (
    @SerializedName("UserName")
    val UserName: String? = null,
    @SerializedName("UserID")
    val UserID: Int? = null,
    @SerializedName("InquiryTotalCount")
    val InquiryTotalCount: Int? = null,
)