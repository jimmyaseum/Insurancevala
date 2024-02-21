package com.app.insurancevala.utils

import android.util.Log

object LogUtil {

    private val showLog = true

    private fun buildHeader(): String {
        val stack = Thread.currentThread().stackTrace[4]
        return if (stack == null) "UNKNOWN" else stack.lineNumber.toString() + "=>"
    }

    fun v(TAG: String, msg: Any) {
        if (showLog) Log.v(TAG, buildHeader() + msg.toString())
    }

    fun d(TAG: String, msg: Any) {
        if (showLog) Log.d(TAG, buildHeader() + msg.toString())
    }

    fun i(TAG: String, msg: Any) {
        if (showLog) Log.i(TAG, buildHeader() + msg.toString())
    }

    fun w(TAG: String, msg: Any) {
        if (showLog) Log.w(TAG, buildHeader() + msg.toString())
    }

    fun e(TAG: String, msg: Any) {
        if (showLog) Log.e(TAG, buildHeader() + msg.toString())
    }
}