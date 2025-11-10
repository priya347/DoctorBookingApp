package com.example.telemedicineapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Doctor(
    val name: String,
    val experience: String,
    val qualification: String,
    val contact: String,
    val mailId: String,
    val category: String,
    val location: String,
    val id: String
) : Parcelable