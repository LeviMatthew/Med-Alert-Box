package com.app.medalertbox.server

// ApiService.kt
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    // Define a GET request
    @GET("users")
    fun getUsers(@Query("page") page: Int): Call<List<User>>
}
