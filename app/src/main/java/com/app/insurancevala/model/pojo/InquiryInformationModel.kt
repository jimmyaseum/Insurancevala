package com.app.insurancevala.model.pojo

import android.widget.CheckBox
import android.widget.EditText
import com.google.gson.annotations.SerializedName

data class InquiryInformationModel (

    @SerializedName("ID")
    var ID: Int = 0,

    @SerializedName("FamilyMember")
    var FamilyMember: String = "",

    @SerializedName("ProposedAmount")
    var ProposedAmount: ArrayList<ProposedAmountInfoModel>? = null,

    @SerializedName("ClosingAmount")
    var ClosingAmount: ArrayList<ClosingAmountInfoModel>? = null,

    @SerializedName("FamilyMemberId")
    var FamilyMemberId: Int = 0,

    @SerializedName("InquirytypeId")
    var InquirytypeId: String = "",

    @SerializedName("Inquirytype")
    var Inquirytype: String = "",

    @SerializedName("InquirysubtypeId")
    var InquirysubtypeId: String = "",

    @SerializedName("Inquirysubtype")
    var Inquirysubtype: String = "",

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

    @SerializedName("CoPersonAllotmentToId")
    var CoPersonAllotmentToId: Int = 0,

    @SerializedName("CoPersonAllotmentTo")
    var CoPersonAllotmentTo: String = "",

    @SerializedName("InquiryDate")
    var InquiryDate: String = "",

    @SerializedName("mInquiryDate")
    var mInquiryDate: String = "",

    //TIL Error handling

    @SerializedName("tilFamilyMember")
    var tilFamilyMember: EditText? = null,

    @SerializedName("tilInquiryType")
    var tilInquiryType: EditText? = null,

    @SerializedName("tilInquirysubtype")
    var tilInquirysubtype: EditText? = null,

    @SerializedName("tilFrequency")
    var tilFrequency: EditText? = null,

    @SerializedName("tilLeadtype")
    var tilLeadtype: EditText? = null,

    @SerializedName("tilLeadstatus")
    var tilLeadstatus: EditText? = null,

    @SerializedName("tilAllotmentTo")
    var tilAllotmentTo: EditText? = null,

    @SerializedName("tilCoPersonAllotmentTo")
    var tilCoPersonAllotmentTo: EditText? = null,

    @SerializedName("tilInquiryDate")
    var tilInquiryDate: EditText? = null,

    )