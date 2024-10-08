package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class CityResponse {
    @SerializedName("Data")
    var Data: ArrayList<CityModel>? = null
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

class CityByGUIDResponse {
    @SerializedName("Data")
    var Data: CityModel? = null
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

data class CityModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("CityGUID")
    val CityGUID: String? = null,
    @SerializedName("CityName")
    val CityName: String? = null,
    @SerializedName("CountryID")
    val CountryID: Int? = null,
    @SerializedName("CountryName")
    val CountryName: String? = null,
    @SerializedName("StateID")
    val StateID: Int? = null,
    @SerializedName("StateName")
    val StateName: String? = null,
    @SerializedName("IsActive")
    val IsActive: Boolean? = null,
    @SerializedName("IsSelected")
    var IsSelected : Boolean? = false
)