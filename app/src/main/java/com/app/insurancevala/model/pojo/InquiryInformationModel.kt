package com.app.insurancevala.model.pojo

import android.widget.CheckBox
import android.widget.EditText
import com.google.gson.annotations.SerializedName

data class InquiryInformationModel (

    @SerializedName("InquirytypeId")
    var InquirytypeId: Int = 0,

    @SerializedName("Inquirytype")
    var Inquirytype: String = "",

    @SerializedName("InquirysubtypeId")
    var InquirysubtypeId: Int = 0,

    @SerializedName("Inquirysubtype")
    var Inquirysubtype: String = "",

    @SerializedName("Proposed")
    var Proposed: String = "",

    @SerializedName("Frequency")
    var Frequency: String = "",

    @SerializedName("LeadtypeId")
    var LeadtypeId: Int = 0,

    @SerializedName("Leadtype")
    var Leadtype: String = "",

    @SerializedName("LeadstatusId")
    var LeadstatusId: Int = 0,

    @SerializedName("Leadstatus")
    var Leadstatus: String = "",

    @SerializedName("AllotmentToId")
    var AllotmentToId: Int = 0,

    @SerializedName("AllotmentTo")
    var AllotmentTo: String = "",

    @SerializedName("InquiryDate")
    var InquiryDate: String = "",

    @SerializedName("mInquiryDate")
    var mInquiryDate: String = "",

    //TIL Error handling

    @SerializedName("tilInquiryType")
    var tilInquiryType: EditText? = null,

    @SerializedName("tilInquirysubtype")
    var tilInquirysubtype: EditText? = null,

    @SerializedName("tilProposed")
    var tilProposed: EditText? = null,

    @SerializedName("tilFrequency")
    var tilFrequency: EditText? = null,

    @SerializedName("tilLeadtype")
    var tilLeadtype: EditText? = null,

    @SerializedName("tilLeadstatus")
    var tilLeadstatus: EditText? = null,

    @SerializedName("tilAllotmentTo")
    var tilAllotmentTo: EditText? = null,

    @SerializedName("tilInquiryDate")
    var tilInquiryDate: EditText? = null,

    )