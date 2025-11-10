package com.example.telemedicineapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val fullName: String,
    val mailID: String,
    val phoneNumber: String,
    val dob: String,
    val gender: String
) : Parcelable
