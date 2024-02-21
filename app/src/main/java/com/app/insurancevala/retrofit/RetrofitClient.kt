package com.app.insurancevala.retrofit

import com.app.insurancevala.BuildConfig
import com.app.insurancevala.utils.AppConstant
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.InetAddress
import java.util.concurrent.TimeUnit


object RetrofitClient {

    var retrofit: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit? {

        if (retrofit != null) {
            retrofit = null
        }

        //TODO While release in Google Play Change the Level to NONE
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }

        val dispatcher = Dispatcher()
        dispatcher.maxRequests = 1

        class OAuthInterceptor(private val tokenType: String, private val acceessToken: String): Interceptor {

            override fun intercept(chain: Interceptor.Chain): Response {
                var request = chain.request()

                if (!isInternetAvailable()) {
                    throw Exception()
                } else {
                    val builder = request.newBuilder()
                    builder?.let { builder ->
                        builder.addHeader("Authorization", "$tokenType $acceessToken")
                    }
                    request = builder.build()

                    return chain.proceed(request)
                }
            }
        }

//        val client = OkHttpClient.Builder()
//            .addInterceptor(interceptor) // comment
//            .addInterceptor(OAuthInterceptor("Bearer", AppConstant.TOKEN))
//            .dispatcher(dispatcher)
//            .connectTimeout(60, TimeUnit.SECONDS)
//            .readTimeout(60, TimeUnit.SECONDS)
//            .writeTimeout(60, TimeUnit.SECONDS)
//            .build()

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .dispatcher(dispatcher)
            .addInterceptor(OAuthInterceptor("Bearer", AppConstant.TOKEN))
            .addInterceptor(logging)
            .build()

        retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit

    }

    private fun isInternetAvailable(): Boolean {
        return try {
            val ipAddr = InetAddress.getByName("google.com")
            //You can replace it with your name
            !ipAddr.equals("")

        } catch (e: Exception) {
            false
        }
    }
}