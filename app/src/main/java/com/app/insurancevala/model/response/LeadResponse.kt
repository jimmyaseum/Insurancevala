package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class LeadResponse {
    @SerializedName("Data")
    var Data: ArrayList<LeadModel>? = null
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

data class LeadModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("LeadGUID")
    val LeadGUID: String? = null,
    @SerializedName("FirstName")
    val FirstName: String? = null,
    @SerializedName("MiddleName")
    val MiddleName: String? = null,
    @SerializedName("LastName")
    val LastName: String? = null,
    @SerializedName("EmailID")
    val EmailID: String? = null,
    @SerializedName("MobileNo")
    var MobileNo : String? = null,
    @SerializedName("GroupCode")
    var GroupCode : String? = "",
    @SerializedName("LeadImage")
    var LeadImage : String? = null,
    @SerializedName("LeadStage")
    val LeadStage: Int? = null,
    @SerializedName("InitialID")
    val InitialID: Int? = null,
    @SerializedName("LeadOwnerID")
    val LeadOwnerID: String? = null,
    @SerializedName("LeadSourceID")
    val LeadSourceID: Int? = null,
    @SerializedName("CityID")
    val CityID: Int? = null,
    @SerializedName("StateID")
    val StateID: Int? = null,
    @SerializedName("CountryID")
    val CountryID: Int? = null,
    @SerializedName("CompanyName")
    val CompanyName: String? = null,
    @SerializedName("Title")
    val Title: String? = null,
    @SerializedName("PhoneNo")
    val PhoneNo: String? = null,
    @SerializedName("Website")
    val Website: String? = null,
    @SerializedName("RefName")
    val RefName: String? = null,
    @SerializedName("RefMobileNo")
    val RefMobileNo: String? = null,
    @SerializedName("Industry")
    val Industry: String? = null,
    @SerializedName("NoofEmp")
    val NoofEmp: String? = null,
    @SerializedName("AnnualRevenue")
    val AnnualRevenue: String? = null,
    @SerializedName("Address")
    val Address: String? = null,
    @SerializedName("PinCode")
    val PinCode: String? = null,
    @SerializedName("Notes")
    val Notes: String? = null,
    @SerializedName("IsActive")
    val IsActive: Boolean? = null,
    @SerializedName("IsSelected")
    var IsSelected : Boolean? = false,
    @SerializedName("TotalAttachment")
    val TotalAttachment: Int? = null,
    @SerializedName("TotalTask")
    val TotalTask: Int? = null,
    @SerializedName("TotalNote")
    val TotalNote: Int? = null,
    @SerializedName("TotalMeeting")
    val TotalMeeting: Int? = null,
    @SerializedName("TotalCall")
    val TotalCall: Int? = null
)