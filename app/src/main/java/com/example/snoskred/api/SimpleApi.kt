package com.example.snoskred.api

import com.example.snoskred.model.Post
import retrofit2.http.GET

interface SimpleApi {

    @GET("Simple")
    suspend fun getPost(): Post


}