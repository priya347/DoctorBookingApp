package com.example.telemedicineapp.client

import com.example.telemedicineapp.model.Doctor
import com.example.telemedicineapp.service.APIService
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://677a97bf671ca0306834584f.mockapi.io/"

    private val instance: APIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIService::class.java)
    }

    fun getDoctorsList(): Call<List<Doctor>> {
        return instance.getDoctorsList()
    }

}