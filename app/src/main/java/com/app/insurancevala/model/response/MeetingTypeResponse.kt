package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class MeetingTypeResponse {
    @SerializedName("Data")
    var Data: ArrayList<MeetingTypeModel>? = null
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

data class MeetingTypeModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("MeetingTypeGUID")
    val MeetingTypeGUID: String? = null,
    @SerializedName("MeetingType")
    val MeetingType: String? = null,
    @SerializedName("IsActive")
    val IsActive: Boolean? = null,
    @SerializedName("IsSelected")
    var IsSelected : Boolean? = false
)
