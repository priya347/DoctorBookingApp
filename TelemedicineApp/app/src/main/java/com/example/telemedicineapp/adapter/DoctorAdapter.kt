package com.example.telemedicineapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.telemedicineapp.databinding.IndividualDoctorBinding
import com.example.telemedicineapp.model.Doctor

class DoctorAdapter(
    private var doctors: List<Doctor>,
    private val onClick: (Doctor) -> Unit
) : RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    inner class DoctorViewHolder(private val binding: IndividualDoctorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(doctor: Doctor) {
            binding.doctorName.text = doctor.name
            binding.doctorCategory.text = doctor.category
            binding.doctorLocation.text = doctor.location
            binding.viewDetailsIcon.setOnClickListener { onClick(doctor) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val binding =
            IndividualDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DoctorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        holder.bind(doctors[position])
    }

    override fun getItemCount(): Int = doctors.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(filteredList: List<Doctor>) {
        doctors = filteredList
        notifyDataSetChanged()
    }
}
