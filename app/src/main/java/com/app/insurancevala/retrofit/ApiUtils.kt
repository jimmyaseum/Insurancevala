package com.app.insurancevala.retrofit

import com.app.insurancevala.utils.AppConstant.BASE_URL


/**
 * Created by Jimmy
 */
object ApiUtils {

    val apiInterface: ApiInterface
        get() = RetrofitClient.getClient(BASE_URL)!!.create(ApiInterface::class.java)
}