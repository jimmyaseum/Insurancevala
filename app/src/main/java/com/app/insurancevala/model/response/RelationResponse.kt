package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class RelationResponse {
    @SerializedName("Data")
    var Data: ArrayList<RelationModel>? = null
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

data class RelationModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("RelationGUID")
    val RelationGUID: String? = null,
    @SerializedName("RelationName")
    val RelationName: String? = null,
    @SerializedName("IsActive")
    val IsActive: Boolean? = null,
    @SerializedName("IsSelected")
    var IsSelected : Boolean? = false
)
