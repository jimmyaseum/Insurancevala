package com.app.insurancevala.model.pojo

import android.widget.CheckBox
import android.widget.EditText
import com.google.gson.annotations.SerializedName

data class FamilyMemberInfoModel(

    @SerializedName("ID")
    var ID: Int = 0,

    @SerializedName("RelationId")
    var RelationId: Int = 0,

    @SerializedName("Relation")
    var Relation: String = "",

    @SerializedName("InitialID")
    var InitialID: Int = 0,

    @SerializedName("Initial")
    var Initial: String = "",

    @SerializedName("FirstName")
    var FirstName: String = "",

    @SerializedName("LastName")
    var LastName: String = "",

    @SerializedName("MobileNo")
    var MobileNo: String = "",

    @SerializedName("DateOfBirth")
    var DateOfBirth: String = "",

    @SerializedName("mDateOfBirth")
    var mDateOfBirth: String = "",

    //TIL Error handling
    @SerializedName("tilRelation")
    var tilRelation: EditText? = null,

    @SerializedName("tilInitial")
    var tilInitial: EditText? = null,

    @SerializedName("tilFirstName")
    var tilFirstName: EditText? = null,

    @SerializedName("tilLastName")
    var tilLastName: EditText? = null,

    @SerializedName("tilMobileNo")
    var tilMobileNo: EditText? = null,

    @SerializedName("tilDateOfBirth")
    var tilDOB: EditText? = null,

    )