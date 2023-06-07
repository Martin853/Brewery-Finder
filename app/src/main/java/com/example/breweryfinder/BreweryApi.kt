package com.example.breweryfinder

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BreweryApi {
    @GET("/v1/breweries")
    suspend fun getBreweries(@Query("per_page") per_page: Int): Response<List<Brewery>>
}