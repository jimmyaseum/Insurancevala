package com.app.insurancevala.model.response

import android.widget.EditText
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PlanResponse {
    @SerializedName("Data")
    var Data: ArrayList<PlanModel>? = null
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

data class PlanModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("PlanName")
    val PlanName: String? = null,
    @SerializedName("IsSelected")
    var IsSelected : Boolean? = false,
)