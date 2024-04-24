package com.app.insurancevala.model.response

import android.widget.EditText
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CompanyResponse {
    @SerializedName("Data")
    var Data: ArrayList<CompanyModel>? = null
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

class CompanyByGUIDResponse {
    @SerializedName("Data")
    var Data: CompanyModel? = null
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

data class CompanyModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("CompanyName")
    val CompanyName: String? = null,
    @SerializedName("PlanList")
    val PlanList: ArrayList<PlanDetailsModel>? = null,
    @SerializedName("IsSelected")
    var IsSelected : Boolean? = false,
)

data class PlanDetailsModel (
    @SerializedName("ID")
    val ID: Int? = null,
    @SerializedName("PlanName")
    val PlanName: String? = null,
    var IsSelected : Boolean? = null
)