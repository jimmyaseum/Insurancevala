package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class FamilyResponse {
    @SerializedName("Data")
    var Data: ArrayList<FamilyModel>? = null
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

data class FamilyModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("InitialName")
    val InitialName: String? = null,
    @SerializedName("FirstName")
    val FirstName: String? = null,
    @SerializedName("LastName")
    val LastName: String? = null,
    @SerializedName("GroupCode")
    val GroupCode: String? = null,
    @SerializedName("IsSelected")
    var IsSelected: Boolean? = null
    )