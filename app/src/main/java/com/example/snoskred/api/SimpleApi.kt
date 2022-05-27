package com.example.snoskred.api

import com.example.snoskred.model.Post
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SimpleApi {

    @GET("/hydrology/forecast/avalanche/v6.0.1/api/AvalancheWarningByCoordinates/Simple/{X}/{Y}/{Spraknokkel}/")
    suspend fun getPost(
        @Path("X") lat: Double,
        @Path("Y") lng: Double,
        @Path("Spraknokkel") Spraknokkel: Int,
    ): Response<List<Post>>


    @GET("/hydrology/forecast/avalanche/v6.0.1/api/AvalancheWarningByRegion/Simple/{RegionID}/{Spraknokkel}/")
    suspend fun getStatPost(
        @Path("RegionID") RegionId: Int,
        @Path("Spraknokkel") Spraknokkel: Int,
    ): Response<List<Post>>



}