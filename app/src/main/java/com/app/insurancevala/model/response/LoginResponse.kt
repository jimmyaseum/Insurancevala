package com.app.insurancevala.model.response

import java.io.Serializable
import com.google.gson.annotations.SerializedName

class LoginResponse : Serializable {

    @SerializedName("Data")
    var Data: LoginModel? = null
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

data class LoginModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("Name")
    val Name: String? = null,
    @SerializedName("UserName")
    val UserName: String? = null,
    @SerializedName("MobileNo")
    val MobileNo: String? = null,
    @SerializedName("EmailID")
    val EmailID: String? = null,
    @SerializedName("DeviceToken")
    val Token: String? = null,
    @SerializedName("IsUser")
    val IsUser: Int? = null,
    @SerializedName("UserTypeID")
    val UserTypeID: Int? = null,
    @SerializedName("UserType")
    val UserType: String? = null,
    @SerializedName("UserGUID")
    val UserGUID: String? = null,
    @SerializedName("Details")
    val Details: String? = null,
    @SerializedName("UserImage")
    val UserImage: String? = null,
):Serializable
