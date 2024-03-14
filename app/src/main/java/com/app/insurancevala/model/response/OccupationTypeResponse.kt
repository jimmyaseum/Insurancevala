package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class OccupationTypeResponse {
    @SerializedName("Data")
    var Data: ArrayList<OccupationTypeModel>? = null
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

class OccupationTypeByGUIDResponse {
    @SerializedName("Data")
    var Data: OccupationTypeModel? = null
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

data class OccupationTypeModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("OccupationGUID")
    val OccupationGUID: String? = null,
    @SerializedName("OccupationName")
    val OccupationName: String? = null,
    @SerializedName("IsActive")
    val IsActive: Boolean? = null,
    @SerializedName("IsSelected")
    var IsSelected : Boolean? = false
)