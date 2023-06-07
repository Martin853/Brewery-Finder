package com.example.breweryfinder

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val api: BreweryApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openbrewerydb.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BreweryApi::class.java)
    }

}