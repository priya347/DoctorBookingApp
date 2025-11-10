package com.example.telemedicineapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookedSlot(
    val id: String = "",
    val selectedDate: String = "",
    val selectedSlot: String = "",
    val doctorName: String = "",
    val doctorSpecialization: String = "",
    val hospitalName: String = ""
) : Parcelable {
    // No-argument constructor required by Firebase
    constructor() : this("", "", "", "", "", "")
}
