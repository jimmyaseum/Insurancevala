package com.app.insurancevala.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.awesomedialog.*
import com.google.android.material.snackbar.Snackbar
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.RequestBody
import java.util.regex.Matcher
import java.util.regex.Pattern


fun RecyclerView.setManager(
    isItHorizontal: Boolean = false,
    isItGrid: Boolean = false,
    spanCount: Int = 2
) {
    if (isItGrid)
        this.layoutManager = GridLayoutManager(this.context, spanCount)
    else {
        if (isItHorizontal)
            this.layoutManager = LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
        else
            this.layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
    }
}

fun getRequestJSONBody(value: String): RequestBody =
    RequestBody.create(MediaType.parse("application/json"), value)

fun Context.toast(message: CharSequence, duration: Int) =
    Toast.makeText(this, message, duration).show()


fun String.toast(context: Context) =
    Toast.makeText(context, this, Toast.LENGTH_LONG).show()

fun String.isValidEmail(): Boolean = this.isNotEmpty() &&
        Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun hideKeyboard(context: Context, view: View?) {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view?.windowToken, 0)
}

fun isOnline(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo = connectivityManager.activeNetworkInfo
    return netInfo != null && netInfo.isConnected
}
fun ImageView.loadURI(uri: Uri, placeholder: Int) {
    Glide.with(context).load(uri).apply(RequestOptions().transform(CenterCrop()).placeholder(placeholder)).into(this)
}
fun ImageView.loadUrl(url: String? = "", placeholder: Int) {
    Glide.with(context).load(url).apply(RequestOptions().placeholder(placeholder)).into(this)
}
// TAG - classname prints in main method only but can't prints in retrofit inner methods or others
val Any.TAG: String
    get() {
        return if (!javaClass.isAnonymousClass) {
            val name = javaClass.simpleName
            if (name.length <= 23) name else name.substring(0, 23)// first 23 chars
        } else {
            val name = javaClass.name
            if (name.length <= 23) name else name.substring(name.length - 23, name.length)// last 23 chars
        }
    }

// TAG() - classname prints in main method only but can't prints in retrofit inner methods or others
/*
inline fun <reified T> T.TAG(): String = T::class.java.simpleName*/


inline fun FragmentManager.addFragmentTransaction(func: FragmentTransaction.() -> Unit) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    fragmentTransaction.commit()
}

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int, tag: String) {
    supportFragmentManager.addFragmentTransaction { add(frameId, fragment, tag) }
}

inline fun FragmentManager.replaceFragmentTransaction(func: FragmentTransaction.() -> Unit) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    fragmentTransaction.commit()
}

fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int, tag: String, addToBackStack: Boolean = false) {
    supportFragmentManager.replaceFragmentTransaction {
        replace(frameId, fragment, tag)
        if (addToBackStack) addToBackStack(fragment.javaClass.name)
    }
}

inline fun <T> justTry(block: () -> T) = try {
    block()
} catch (e: Throwable) {
    e.localizedMessage
}

inline fun <reified T> genericType() = object : TypeToken<T>() {}.type

fun View.isVisible() = this.visibility == View.VISIBLE

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun preventTwoClick(view: View) {
    view.isEnabled = false
    view.postDelayed({ view.isEnabled = true }, 2000)
}

fun isConnectivityAvailable(context: Context): Boolean {
    var activeNetwork: NetworkInfo? = null
    try {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        activeNetwork = connectivityManager.activeNetworkInfo
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return !(activeNetwork == null || !activeNetwork.isConnectedOrConnecting)
}

fun isValidPanCardNo(panCardNo: String?): Boolean {

    // Regex to check valid PAN Card number.
    val regex = "[A-Z]{5}[0-9]{4}[A-Z]{1}"

    // Compile the ReGex
    val p: Pattern = Pattern.compile(regex)

    // If the PAN Card number
    // is empty return false
    if (panCardNo == null) {
        return false
    }

    // Pattern class contains matcher() method
    // to find matching between given
    // PAN Card number using regular expression.
    val m: Matcher = p.matcher(panCardNo)

    // Return if the PAN Card number
    // matched the ReGex
    return m.matches()
}

fun isValidGSTNo(str: String?): Boolean {
    // Regex to check valid
    // GST (Goods and Services Tax) number
    val regex = ("^[0-9]{2}[A-Z]{5}[0-9]{4}"
            + "[A-Z]{1}[1-9A-Z]{1}"
            + "Z[0-9A-Z]{1}$")

    // Compile the ReGex
    val p = Pattern.compile(regex)

    // If the string is empty
    // return false
    if (str == null) {
        return false
    }

    // Pattern class contains matcher()
    // method to find the matching
    // between the given string
    // and the regular expression.
    val m = p.matcher(str)

    // Return if the string
    // matched the ReGex
    return m.matches()
}

fun isValidPassportNo(str: String?): Boolean {
    // Regex to check valid.
    // passport number of India
    val regex = ("^[A-PR-WYa-pr-wy][1-9]\\d"
            + "\\s?\\d{4}[1-9]$")

    // Compile the ReGex
    val p = Pattern.compile(regex)

    // If the string is empty
    // return false
    if (str == null) {
        return false
    }

    // Find match between given string
    // and regular expression
    // using Pattern.matcher()
    val m = p.matcher(str)

    // Return if the string
    // matched the ReGex
    return m.matches()
}

fun isValidAadhaarNumber(str: String?) : Boolean {
    // Regex to check valid Aadhaar number.
    val regex = "^[2-9]{1}[0-9]{3}\\s[0-9]{4}\\s[0-9]{4}$"

    // Compile the ReGex
    val p = Pattern.compile(regex)

    // If the string is empty
    // return false
    if (str == null) {
        return false
    }

    // Pattern class contains matcher() method
    // to find matching between given string
    // and regular expression.
    val m = p.matcher(str)

    // Return if the string
    // matched the ReGex
    return m.matches()
}

fun isValidPassword(password: String?): Boolean {
    val pattern: Pattern
    val matcher: Matcher
    val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
    pattern = Pattern.compile(PASSWORD_PATTERN)
    matcher = pattern.matcher(password)
    return matcher.matches()
}

fun internetErrordialog (context: Context) {
    AwesomeDialog.build(context as Activity)
        .title("Whoops !")
        .body("No internet connection found. Check your connection and try again.")
        .icon(R.drawable.abc_ic_clear_material)
        .position(AwesomeDialog.POSITIONS.CENTER)
        .onNegative("Close") {
        }
}

fun errortint(context: Context): Drawable? {
    val icon = AppCompatResources.getDrawable(context, com.app.insurancevala.R.drawable.ic_error)
    if (icon != null) {
        DrawableCompat.setTint(icon, Color.parseColor("#E5775A"))
        icon.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
    }
    return icon
}