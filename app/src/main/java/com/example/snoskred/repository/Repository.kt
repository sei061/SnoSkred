package com.example.snoskred.repository

import com.example.snoskred.api.RetrofitInstance
import com.example.snoskred.model.Post

class Repository {

    suspend fun getPost(): Post {
        return RetrofitInstance.api.getPost()
    }
}