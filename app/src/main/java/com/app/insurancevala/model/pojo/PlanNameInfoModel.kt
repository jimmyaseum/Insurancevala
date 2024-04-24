package com.app.insurancevala.model.pojo

import android.widget.EditText
import com.google.gson.annotations.SerializedName

data class PlanNameInfoModel(

    @SerializedName("ID")
    val ID: Int? = 0,

    @SerializedName("PlanName")
    var PlanName: String? = "",

    //TIL Error handling
    @SerializedName("tilPlanName")
    var tilPlanName: EditText? = null,

    )