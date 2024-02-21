package com.app.insurancevala.utils

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

fun Date.toMillis(): Long {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.timeInMillis
}

fun getCurrentDate(formatPatter: String): String {
    val sdf = SimpleDateFormat(formatPatter)
    return sdf.format(java.util.Date())
}

fun String.convertStringToInputFormat(simpleDateFormatPattern: String): Date? {
    val simpleDateFormat = SimpleDateFormat(simpleDateFormatPattern, Locale.getDefault())
    var value: Date? = null
    justTry {
        value = simpleDateFormat.parse(this)
    }
    return value
}

fun convertDateStringToString(strDate: String, currentFormat: String): String? {
    return convertDateToString(convertStringToDate(strDate, currentFormat), currentFormat)
}

fun convertDateStringToString(strDate: String, inputFormat: String, outputFormat: String): String? {
    return convertDateToString(convertStringToDate(strDate, inputFormat), outputFormat)
}

fun getcurrentdate(): String {

    var dateTime: String
    var calendar: Calendar
    var simpleDateFormat: SimpleDateFormat


    calendar = Calendar.getInstance()
    simpleDateFormat = SimpleDateFormat(AppConstant.dd_MM_yyyy_HH_mm_ss)
    dateTime = simpleDateFormat.format(calendar.time).toString()

    return dateTime
}

fun convertDateToString(objDate: Date?, parseFormat: String): String {
    var value: String = ""
    justTry {
        return SimpleDateFormat(parseFormat).format(objDate)
    }
    return value
}

fun convertStringToDate(strDate: String, currentFormat: String): Date? {
    val simpleDateFormat = SimpleDateFormat(currentFormat, Locale.getDefault())
    //It only set for ISO date format otherwise set as per device's format
    if (currentFormat == AppConstant.dd_MM_yyyy_Slash) {
        simpleDateFormat.timeZone = TimeZone.getTimeZone(Locale.getDefault().toString())
    }
    var value: Date? = null
    justTry {
        value = simpleDateFormat.parse(strDate)
    }
    return value
}

fun convertDefaultDateString(strDate: String): String? {
    return convertDateToString(
        convertStringToDate(strDate, AppConstant.yyyy_MM_dd_Dash),
        AppConstant.yyyy_MM_dd_Dash
    )
}

fun convertMonthYearString(strDate: String): String? {
    return convertDateToString(
        convertStringToDate(strDate, AppConstant.yyyy_MM_dd_Dash),
        AppConstant.yyyy_MM_dd_Dash
    )
}

fun calculateDays(startDate: String, endDate: String, format: String): Long {

    val simpleDateFormat = SimpleDateFormat(format)

    try {
        val dateBefore = simpleDateFormat.parse(startDate)
        val dateAfter = simpleDateFormat.parse(endDate)
        val difference = dateAfter.time - dateBefore.time
        val daysBetween = (difference / (1000 * 60 * 60 * 24))

        return daysBetween
    } catch (e: Exception) {
        e.printStackTrace()
        return 0
    }
}

fun compareDate(date1: java.util.Date, date2: java.util.Date) = when {
    date1 < date2 -> AppConstant.BEFORE
    date1 > date2 -> AppConstant.AFTER
    else -> AppConstant.EQUAL
}

fun getFormattedMonth(startDate: String, endDate: String, format: String): String {

    val sb = StringBuffer()
    val simpleDateFormat = SimpleDateFormat(format)
    val dateBefore = simpleDateFormat.parse(startDate)
    val dateAfter = simpleDateFormat.parse(endDate)

    var diffInSeconds = abs(((dateAfter.time - dateBefore.time) / 1000).toInt())

    val sec = if (diffInSeconds >= 60) (diffInSeconds % 60) else diffInSeconds
    diffInSeconds /= 60
    val min = if (diffInSeconds >= 60) (diffInSeconds % 60) else diffInSeconds
    diffInSeconds /= 60
    val hrs = if (diffInSeconds >= 24) (diffInSeconds % 24) else diffInSeconds
    diffInSeconds /= 24
    val days = if (diffInSeconds >= 30) (diffInSeconds % 30) else diffInSeconds
    diffInSeconds /= 30
    val months = if (diffInSeconds >= 12) (diffInSeconds % 12) else diffInSeconds
    diffInSeconds /= 12
    val years = diffInSeconds

    if (years > 0) {
        if (years == 1) {
            sb.append("1 year")
        } else {
            sb.append("$years years")
        }
        if (years <= 6 && months > 0) {
            if (months == 1) {
                sb.append(", 1 month")
            } else {
                sb.append(", $months months")
            }
        }
        if (months <= 6 && days > 0) {
            if (days == 1) {
                sb.append(", 1 day")
            } else {
                sb.append(", $days days")
            }
        }
    } else if (months > 0) {
        if (months == 1) {
            sb.append("1 month")
        } else {
            sb.append("$months months")
        }
        if (months <= 6 && days > 0) {
            if (days == 1) {
                sb.append(", 1 day")
            } else {
                sb.append(", $days days")
            }
        }
    } else if (days > 0) {
            if (days == 1) {
                sb.append("a day")
            } else {
                sb.append("$days days")
            }

        }
    return sb.toString()
}

fun getFormattedYear(startDate: String, endDate: String, format: String): String {

    val sb = StringBuffer()
    val simpleDateFormat = SimpleDateFormat(format)
    val dateBefore = simpleDateFormat.parse(startDate)
    val dateAfter = simpleDateFormat.parse(endDate)

    var diffInSeconds = abs(((dateAfter.time - dateBefore.time) / 1000).toInt())

    val sec = if (diffInSeconds >= 60) (diffInSeconds % 60) else diffInSeconds
    diffInSeconds /= 60
    val min = if (diffInSeconds >= 60) (diffInSeconds % 60) else diffInSeconds
    diffInSeconds /= 60
    val hrs = if (diffInSeconds >= 24) (diffInSeconds % 24) else diffInSeconds
    diffInSeconds /= 24
    val days = if (diffInSeconds >= 30) (diffInSeconds % 30) else diffInSeconds
    diffInSeconds /= 30
    val months = if (diffInSeconds >= 12) (diffInSeconds % 12) else diffInSeconds
    diffInSeconds /= 12
    val years = diffInSeconds

    if (years > 0) {
        if (years == 1) {
            sb.append("1")
        } else {
            sb.append("$years")
        }
    }
    return sb.toString()
}

fun getFormattedTime(startDate: String, endDate: String, format: String): String {

    val sb = StringBuffer()
    val simpleDateFormat = SimpleDateFormat(format)
    val dateBefore = simpleDateFormat.parse(startDate)
    val dateAfter = simpleDateFormat.parse(endDate)

    var diffInSeconds = abs(((dateAfter.time - dateBefore.time) / 1000).toInt())

    val sec = if (diffInSeconds >= 60) (diffInSeconds % 60) else diffInSeconds
    diffInSeconds /= 60
    val min = if (diffInSeconds >= 60) (diffInSeconds % 60) else diffInSeconds
    diffInSeconds /= 60
    val hrs = if (diffInSeconds >= 24) (diffInSeconds % 24) else diffInSeconds
    diffInSeconds /= 24
    val days = if (diffInSeconds >= 30) (diffInSeconds % 30) else diffInSeconds
    diffInSeconds /= 30
    val months = if (diffInSeconds >= 12) (diffInSeconds % 12) else diffInSeconds
    diffInSeconds /= 12
    val years = diffInSeconds

    if (years > 0) {
        if (years == 1) {
            sb.append("1 year")
        } else {
            sb.append("$years years")
        }
        if (years <= 6 && months > 0) {
            if (months == 1) {
                sb.append(", 1 month")
            } else {
                sb.append(", $months months")
            }
        }
    }
    else if (months > 0) {
        if (months == 1) {
            sb.append("1 month")
        } else {
            sb.append("$months months")
        }
//        if (months <= 6 && days > 0) {
//            if (days == 1) {
//                sb.append(", 1 day")
//            } else {
//                sb.append(", $days days")
//            }
//        }
    } else{
        sb.append("$months months")
    }

//    else if (days > 0) {
//            if (days == 1) {
//                sb.append("a day")
//            } else {
//                sb.append("$days days")
//            }
//            if (days <= 3 && hrs > 0) {
//                if (hrs == 1) {
//                    sb.append(" and an hour")
//                } else {
//                    sb.append(" and $hrs hours")
//                }
//            }
//        } else if (hrs > 0) {
//            if (hrs == 1) {
//                sb.append("an hour")
//            } else {
//                sb.append("$hrs hours")
//            }
//            if (min > 1) {
//                sb.append(" and $min minutes")
//            }
//        } else if (min > 0) {
//            if (min == 1) {
//                sb.append("a minute")
//            } else {
//                sb.append("$min minutes")
//            }
//            if (sec > 1) {
//                sb.append(" and $sec seconds")
//            }
//        } else {
//            if (sec <= 1) {
//                sb.append("about a second")
//            } else {
//                sb.append("about $sec seconds")
//            }
//        }

//        sb.append(" ago")

    return sb.toString()
}