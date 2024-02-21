package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class CountriesResponse {
    @SerializedName("Data")
    var Data: ArrayList<CountriesModel>? = null
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

data class CountriesModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("CountryGUID")
    val CountryGUID: String? = null,
    @SerializedName("CountryName")
    val CountryName: String? = null,
    @SerializedName("IsActive")
    val IsActive: Boolean? = null,
    @SerializedName("IsSelected")
    var IsSelected : Boolean? = false
)