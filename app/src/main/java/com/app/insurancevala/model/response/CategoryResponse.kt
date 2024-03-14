package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

class CategoryResponse {
    @SerializedName("Data")
    var Data: ArrayList<CategoryModel>? = null
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

data class CategoryModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("CategoryGUID")
    val CategoryGUID: String? = null,
    @SerializedName("CategoryName")
    val CategoryName: String? = null,
    @SerializedName("IsActive")
    val IsActive: Boolean? = null,
    @SerializedName("IsSelected")
    var IsSelected : Boolean? = false
)
