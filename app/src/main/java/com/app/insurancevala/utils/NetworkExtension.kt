package com.app.insurancevala.utils

import com.app.insurancevala.retrofit.ApiResponse
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


fun <T> Call<T>.enqueueCall(callback: CallBackKt<T>.() -> Unit) {
    val callBackKt = CallBackKt<T>()
    callback.invoke(callBackKt)
    this.enqueue(callBackKt)

}

class CallBackKt<T> : Callback<T> {

    var onResponse: ((ApiResponse<T>) -> Unit)? = null
    // var onFailure: ((t: ApiResponse<Throwable>) -> Unit)? = null

    override fun onFailure(call: Call<T>, t: Throwable) {
        var response = ApiResponse(
            status = false,
            message = t.message!!,
            data = t as T
        )
        onResponse?.invoke(response)
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        var response = ApiResponse(
            status = response.isSuccessful,
            message = if (response.errorBody() == null) response.message() else response.errorBody()?.getError()!!,
            data = response.body()
        )


        onResponse?.invoke(response)
    }

}

fun ResponseBody.getError(): String? {
    return try {
        val jObjError = JSONObject(this.string())
        jObjError["message"].toString()

    } catch (e: Exception) {
        e.message
    }.toString()
}