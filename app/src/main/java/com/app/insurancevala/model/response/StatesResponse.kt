package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class StatesResponse {
    @SerializedName("Data")
    var Data: ArrayList<StatesModel>? = null
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

data class StatesModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("StateGUID")
    val StateGUID: String? = null,
    @SerializedName("StateName")
    val StateName: String? = null,
    @SerializedName("CountryID")
    val CountryID: Int? = null,
    @SerializedName("CountryName")
    val CountryName: String? = null,
    @SerializedName("IsActive")
    val IsActive: Boolean? = null,
    @SerializedName("IsSelected")
    var IsSelected : Boolean? = false
)

