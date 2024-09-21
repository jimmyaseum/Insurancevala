package com.app.insurancevala.model.pojo

import android.text.Editable
import android.widget.EditText
import com.google.gson.annotations.SerializedName

data class ProposedAmountInfoModel(

    @SerializedName("ID")
    val ID: Int? = 0,

    @SerializedName("ProspectAmount")
    var ProspectAmount: String? = "",

    //TIL Error handling
    @SerializedName("tilProspectAmount")
    var tilProspectAmount: EditText? = null,

    )