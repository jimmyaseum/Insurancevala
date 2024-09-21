package com.app.insurancevala.model.pojo

import android.text.Editable
import android.widget.EditText
import com.google.gson.annotations.SerializedName

data class ClosingAmountInfoModel(

    @SerializedName("ID")
    val ID: Int? = 0,

    @SerializedName("ClosingAmount")
    var ClosingAmount: String? = "",

    //TIL Error handling
    @SerializedName("tilClosingAmount")
    var tilClosingAmount: EditText? = null,

    )