package com.app.insurancevala.utils

import android.content.Context
import android.content.SharedPreferences
import com.app.insurancevala.utils.AppConstant.PREF_NAME


class SharedPreference(val context: Context) {

    private val sharedPreference: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun setPreference(KEY_NAME: String, value: String) {
        val editor: SharedPreferences.Editor = sharedPreference.edit()
        editor.putString(KEY_NAME, value)
        editor.commit()
    }

    fun setPreference(KEY_NAME: String, value: Int) {
        val editor: SharedPreferences.Editor = sharedPreference.edit()
        editor.putInt(KEY_NAME, value)
        editor.commit()
    }

    fun setPreference(KEY_NAME: String, value: Boolean) {
        val editor: SharedPreferences.Editor = sharedPreference.edit()

        editor.putBoolean(KEY_NAME, value)

        editor.commit()
    }

    fun getPreferenceString(KEY_NAME: String): String? {
        return sharedPreference.getString(KEY_NAME, null)
    }

    fun getPreferenceInt(KEY_NAME: String): Int {
        return sharedPreference.getInt(KEY_NAME, 0)
    }

    fun getPreferenceBoolean(KEY_NAME: String, defaultValue: Boolean): Boolean {
        return sharedPreference.getBoolean(KEY_NAME, defaultValue)
    }

    fun clearSharedPreference() {
        val editor: SharedPreferences.Editor = sharedPreference.edit()
        //sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        editor.clear()
        editor.commit()
    }

    fun removeValue(KEY_NAME: String) {
        val editor: SharedPreferences.Editor = sharedPreference.edit()
        editor.remove(KEY_NAME)
        editor.commit()
    }
}

