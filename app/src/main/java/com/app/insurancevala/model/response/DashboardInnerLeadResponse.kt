package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class DashboardInnerLeadResponse {
    @SerializedName("Data")
    var Data: ArrayList<DashboardInnerLeadModel>? = null

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

data class DashboardInnerLeadModel(
    @SerializedName("ID")
    var ID: Int? = null,
    @SerializedName("LeadGUID")
    var LeadGUID: String? = null,
    @SerializedName("LeadStage")
    var LeadStage: Int? = null,
    @SerializedName("InitialID")
    var InitialID: Int? = null,
    @SerializedName("InitialName")
    var InitialName: String? = null,
    @SerializedName("FirstName")
    var FirstName: String? = null,
    @SerializedName("MiddleName")
    var MiddleName: String? = null,
    @SerializedName("LastName")
    var LastName: String? = null,
    @SerializedName("MobileNo")
    var MobileNo: String? = null,
    @SerializedName("EmailID")
    var EmailID: String? = null,
    @SerializedName("CategoryID")
    var CategoryID: Int? = null,
    @SerializedName("GroupCode")
    var GroupCode: String? = null,
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
