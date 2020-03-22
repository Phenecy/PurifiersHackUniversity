package dev.bonch.herehackpurify.model.network


import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkService {
    val TABLE_API: JsonApi
    private val retrofit: Retrofit

    init{
        val okHttpClient = OkHttpClient.Builder().build()

        retrofit = Retrofit.Builder()
            .baseUrl("https://cleaners-hack-here.herokuapp.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        TABLE_API = retrofit.create(
            JsonApi::class.java)
    }
}
