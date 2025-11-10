package com.example.telemedicineapp.service

import com.example.telemedicineapp.model.Doctor
import retrofit2.Call
import retrofit2.http.GET

interface APIService {
    @GET("doctors_list")
    fun getDoctorsList(): Call<List<Doctor>>
}