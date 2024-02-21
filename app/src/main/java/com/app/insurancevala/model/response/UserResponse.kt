package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class UserResponse {
    @SerializedName("Data")
    var Data: ArrayList<UserModel>? = null
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

data class UserModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("UserGUID")
    val UserGUID: String? = null,
    @SerializedName("ReferenceGUID")
    val ReferenceGUID: String? = null,
    @SerializedName("FirstName")
    val FirstName: String? = null,
    @SerializedName("LastName")
    val LastName: String? = null,
    @SerializedName("MobileNo")
    val MobileNo: String? = null,
    @SerializedName("AlternateMobileNo")
    val AlternateMobileNo: String? = null,
    @SerializedName("EmailID")
    val EmailID: String? = null,
    @SerializedName("AlternateEmailID")
    val AlternateEmailID: String? = null,
    @SerializedName("Password")
    val Password: String? = null,
    @SerializedName("UserTypeID")
    val UserTypeID: Int? = null,
    @SerializedName("UserType")
    val UserType: String? = null,
    @SerializedName("IsActive")
    val IsActive: Boolean? = null,
    @SerializedName("IsSelected")
    var IsSelected : Boolean? = false,
    @SerializedName("UserImage")
    val UserImage: String? = null,
)
