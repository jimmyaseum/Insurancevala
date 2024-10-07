package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class LeadResponse {
    @SerializedName("Data")
    var Data: ArrayList<LeadModel>? = null
    @SerializedName("ExistingCount")
    var ExistingCount: String? = null
    @SerializedName("ProspectCount")
    var ProspectCount: String? = null
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

class LeadByGUIDResponse {
    @SerializedName("Data")
    var Data: LeadModel? = null
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
    @SerializedName("ExistingCount")
    var ExistingCount: Int? = null,
    @SerializedName("ProspectCount")
    var ProspectCount: Int? = null,
    @SerializedName("SearchCount")
    var SearchCount: Int? = null,
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
    @SerializedName("CreatedByName")
    val CreatedByName: String? = null,
    @SerializedName("MobileNo")
    var MobileNo : String? = null,
    @SerializedName("CategoryName")
    var CategoryName : String? = null,
    @SerializedName("GroupCode")
    var GroupCode : String? = "",
    @SerializedName("LeadImage")
    var LeadImage : String? = null,
    @SerializedName("LeadStage")
    val LeadStage: Int? = null,
    @SerializedName("InitialID")
    val InitialID: Int? = null,
    @SerializedName("CategoryID")
    val CategoryID: Int? = null,
    @SerializedName("IsFamilyDetails")
    val IsFamilyDetails: Boolean? = null,
    @SerializedName("OccupationID")
    val OccupationID: Int? = null,
    @SerializedName("LeadOwnerID")
    val LeadOwnerID: Int? = null,
    @SerializedName("LeadOwnerName")
    val LeadOwnerName: String? = null,
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
    @SerializedName("BirthDate")
    val BirthDate: String? = null,
    @SerializedName("MarriageDate")
    val MarriageDate: String? = null,
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
    val TotalCall: Int? = null,
    @SerializedName("MainImage")
    val MainImage: String? = null,
    @SerializedName("OtherImage")
    val OtherImage: String? = null,
    @SerializedName("FamilysDetails")
    val FamilyDetails: ArrayList<FamilyDetailsModel>? = null
):Serializable

data class FamilyDetailsModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("InitialID")
    val InitialID: Int? = null,
    @SerializedName("InitialName")
    val InitialName: String? = null,
    @SerializedName("RelationID")
    val RelationID: Int? = null,
    @SerializedName("RelationName")
    val RelationName: String? = null,
    @SerializedName("FirstName")
    val FirstName: String? = null,
    @SerializedName("LastName")
    val LastName: String? = null,
    @SerializedName("MobileNo")
    val MobileNo: String? = null,
    @SerializedName("BirthDate")
    val BirthDate: String? = null,
    @SerializedName("GroupCode")
    val GroupCode: String? = null,
    @SerializedName("IsSelected")
    var IsSelected : Boolean? = null
):Serializable
