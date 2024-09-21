package com.app.insurancevala.model.pojo

import android.widget.EditText
import com.google.gson.annotations.SerializedName

data class InquiryTypeModel(

    @SerializedName("ID")
    val ID: Int? = 0,

    @SerializedName("InquiryType")
    var InquiryType: String? = "",

    )