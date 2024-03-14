package com.app.insurancevala.utils

object AppConstant {

    var TOKEN : String = ""

//    val BASE_URL = "http://appapi.mitulthakkar.com/api/" //  AI005 Live URL For App

    val BASE_URL = "http://68.64.173.183/insurancevalaapi/api/" //  AI005 Test URL For Test App

    internal val PREF_NAME = "insurancevala_pref"

    const val TOAST_LONG = 1
    const val TOAST_SHORT = 0

    const val DEVICE_TYPE = 1
    val INTENT_1001 = 1001
    val INTENT_1002 = 1002
    val INTENT_1003 = 1003

    //Date format
    val DATE_INPUT_FORMAT = "dd/MM/yyyy"
    val DEFAULT_DATE_FORMAT = "yyyy-MM-dd" //Default Date Object Format
    val DEFAULT_DATE_FORMAT2 = "yyyy/MM/dd" //Default Date Object Format
    val MONTH_YEAR_INPUT_FORMAT = "MM/yyyy"
    val dd_MM_yyyy_HH_mm_ss = "dd-MM-yyyy HH:mm:ss" // 27-10-2023 15:30:20
    val ddMMyyyy_HHmmss = "MM/dd/yyyy HH:mm:ss" // 27-10-2023 15:30:20
    val dd_LLL_yyyy = "dd-LLL-yyyy" // 27-oct-2023
    val HH_mm_aaa = "HH:mm aaa" //  03:30 pm
    val ddmmmyyyy: String = "dd MMM yyyy"
    val ddmmmyyyy_: String = "dd MMM, yyyy"
    val dd: String = "dd"
    val YYYY_MM_dd_Slash: String = "yyyy/MM/dd"
    val yyyy_MM_dd_Dash: String = "yyyy-MM-dd"
    val dd_MM_yyyy_Slash: String = "dd/MM/yyyy"
    const val DD_MMM_YYYY_FORMAT = "MMMM, dd yyyy"
    const val HH_MM_AA_FORMAT = "hh:mm aa" //04:30 PM
    const val HH_MM_FORMAT = "HH:mm" //16:30
    const val HH_MM_SS_FORMAT = "HH:mm:ss" //16:30:00
    const val day_d_MM = "EEE, d MMM"
    const val day_d_MM_YYYY = "EEE, d MMM yyyy"

    //Date comparision
    const val BEFORE = "BEFORE"
    const val AFTER = "AFTER"
    const val EQUAL = "EQUAL"

    // OperationType
    const val INSERT = 1
    const val EDIT = 2
    const val DELETE = 3
    const val GETBYGUID = 4
    const val GETALLACTIVEWITHFILTER = 5
    const val GETALL = 6
    const val ACTIVEINACTIVE = 7
    const val STATUSUPDATE = 8

    const val STATE = "STATE"
    const val S_ADD = "ADD"
    const val S_EDIT = "EDIT"

    const val NOTE = "Note"
    const val MEETING = "Meeting"
    const val TASK = "Task"
    const val OTHER = "Other"

    const val TotalLead = 2
    const val OwnLead = 3
    const val InquiryType = 4
    const val LeadStatus = 5
    const val Employee = 6
}
