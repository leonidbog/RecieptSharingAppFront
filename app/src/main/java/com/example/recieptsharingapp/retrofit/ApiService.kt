package com.example.recieptsharingapp.retrofit


import com.example.recieptsharingapp.model.dto.LoginRequest
import com.example.recieptsharingapp.model.dto.LoginResponse
import com.example.recieptsharingapp.model.dto.RegisterRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/users/register")
    fun register(@Body request: RegisterRequest): Call<String> // Adjust response type if needed

    @POST("api/users/login") // Assuming you have a login endpoint
    fun login(@Body request: LoginRequest): Call<String>
}