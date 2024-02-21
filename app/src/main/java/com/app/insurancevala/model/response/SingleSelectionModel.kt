package com.app.insurancevala.model.response

import com.google.gson.annotations.SerializedName

data class SingleSelectionModel  (
    @SerializedName("ID")
    val ID : Int? = null,
    @SerializedName("Name")
    val Name : String? = null,
    @SerializedName("IsSelected")
    var IsSelected : Boolean? = false
)