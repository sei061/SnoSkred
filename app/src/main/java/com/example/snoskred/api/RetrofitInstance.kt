package com.example.snoskred.api

import com.example.snoskred.utils.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


object RetrofitInstance {

    private val retrofit by lazy  {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    }

    val api: SimpleApi by lazy {
        retrofit.create(SimpleApi::class.java)
    }
}