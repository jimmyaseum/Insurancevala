package com.app.insurancevala

import android.app.Application
import android.content.Context

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        application = this
    }

    companion object {
        var application: Context? = null
    }
}