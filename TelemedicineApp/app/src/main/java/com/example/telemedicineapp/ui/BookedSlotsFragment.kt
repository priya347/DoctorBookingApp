package com.example.telemedicineapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.telemedicineapp.R
import com.example.telemedicineapp.adapter.BookedSlotsAdapter
import com.example.telemedicineapp.databinding.FragmentBookedSlotsBinding
import com.example.telemedicineapp.di.SlotsReference
import com.example.telemedicineapp.model.BookedSlot
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BookedSlotsFragment : Fragment() {

    private var _binding: FragmentBookedSlotsBinding? = null
    private val binding get() = _binding!!

    @Inject
    @SlotsReference
    lateinit var slotsDbRef: DatabaseReference

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var bookedSlotsRecyclerView: RecyclerView
    private lateinit var bookedSlotsAdapter: BookedSlotsAdapter
    private val bookedSlotsList = mutableListOf<Pair<String, BookedSlot>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookedSlotsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = binding.bookedSlotsToolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        setHasOptionsMenu(true)

        bookedSlotsRecyclerView = binding.bookedSlotsRecyclerView
        bookedSlotsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        bookedSlotsAdapter = BookedSlotsAdapter(bookedSlotsList) { parentId ->
            deleteSlotFromFirebase(parentId)
        }

        binding.bookedSlotsRecyclerView.adapter = bookedSlotsAdapter

        fetchSlotsFromFirebase()
    }

    private fun fetchSlotsFromFirebase() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        slotsDbRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                bookedSlotsList.clear()

                for (slotSnapshot in snapshot.children) {
                    val parentId = slotSnapshot.key ?: continue
                    val bookedSlot = slotSnapshot.getValue(BookedSlot::class.java) ?: continue
                    bookedSlotsList.add(Pair(parentId, bookedSlot))
                }

                bookedSlotsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to fetch slots")
            }
        })
    }

    private fun deleteSlotFromFirebase(parentId: String) {
        val userId = firebaseAuth.currentUser?.uid ?: return

        slotsDbRef.child(userId).child(parentId).removeValue()
            .addOnSuccessListener {
                showToast("Slot deleted")
                removeSlotFromList(parentId)
            }
            .addOnFailureListener {
                showToast("Failed to delete slot")
            }
    }

    private fun removeSlotFromList(parentId: String) {
        val index = bookedSlotsList.indexOfFirst { it.first == parentId }
        if (index != -1) {
            bookedSlotsList.removeAt(index)
            bookedSlotsAdapter.notifyItemRemoved(index)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}