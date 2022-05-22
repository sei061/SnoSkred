package com.example.snoskred.repository

import com.example.snoskred.api.RetrofitInstance
import com.example.snoskred.model.Post
import retrofit2.Response

class Repository {

    suspend fun getPost(lat:Double, lng:Double,
                        Språknøkkel:Int): Response<List<Post>> {
        return RetrofitInstance.api.getPost(lat, lng, Språknøkkel)
    }
}