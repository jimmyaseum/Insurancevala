package com.app.insurancevala.retrofit

import com.app.insurancevala.utils.AppConstant
import com.app.insurancevala.utils.AppConstant.BASE_URL
import com.google.gson.GsonBuilder
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.InetAddress
import java.util.concurrent.TimeUnit

class NetworkRepo {

    private val mService: ApiInterface
    private var mRetrofit: Retrofit? = null

    init {
        mService = createService()
    }

    class OAuthInterceptor(private val tokenType: String, private val acceessToken: String): Interceptor {

        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
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

        fun isInternetAvailable(): Boolean {
            return try {
                val ipAddr = InetAddress.getByName("google.com")
                //You can replace it with your name
                !ipAddr.equals("")

            } catch (e: Exception) {
                false
            }
        }

    }

    private val client: OkHttpClient

    get() {
            val dispatcher = Dispatcher()
            dispatcher.maxRequests = 1


            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            return OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .dispatcher(dispatcher)
                .addInterceptor(OAuthInterceptor("Bearer", AppConstant.TOKEN))
                .addNetworkInterceptor(interceptor) // comment
                .build()
        }

    private fun createService(): ApiInterface {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        if (mRetrofit == null) {
            mRetrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
        }
        return mRetrofit!!.create(ApiInterface::class.java)
    }



}