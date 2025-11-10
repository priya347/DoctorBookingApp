package com.example.telemedicineapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.telemedicineapp.databinding.IndividualBookedSlotBinding
import com.example.telemedicineapp.model.BookedSlot

class BookedSlotsAdapter(
    private var bookedSlots: MutableList<Pair<String, BookedSlot>>, // (parentId, slotData)
    private val onDeleteClick: (String) -> Unit // Only pass parentId
) : RecyclerView.Adapter<BookedSlotsAdapter.BookedSlotViewHolder>() {

    inner class BookedSlotViewHolder(private val binding: IndividualBookedSlotBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(parentId: String, bookedSlot: BookedSlot) {
            binding.selectedDate.text = bookedSlot.selectedDate
            binding.selectedSlot.text = bookedSlot.selectedSlot
            binding.doctorName.text = bookedSlot.doctorName
            binding.doctorCategory.text = bookedSlot.doctorSpecialization
            binding.doctorLocation.text = bookedSlot.hospitalName

            binding.trashLayout.setOnClickListener {
                onDeleteClick(parentId) // Pass parentId for deletion
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookedSlotViewHolder {
        val binding = IndividualBookedSlotBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BookedSlotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookedSlotViewHolder, position: Int) {
        val (parentId, bookedSlot) = bookedSlots[position]
        holder.bind(parentId, bookedSlot)
    }

    override fun getItemCount(): Int = bookedSlots.size
}
