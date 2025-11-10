package com.example.telemedicineapp.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.example.telemedicineapp.R
import com.example.telemedicineapp.databinding.BottomSheetDialogBinding
import com.example.telemedicineapp.databinding.DialogCalendarBinding
import com.example.telemedicineapp.databinding.FragmentDoctorDetailBinding
import com.example.telemedicineapp.di.SlotsReference
import com.example.telemedicineapp.model.BookedSlot
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class DoctorDetailFragment : Fragment() {

    private var _binding: FragmentDoctorDetailBinding? = null
    private val binding get() = _binding!!

    @Inject
    @SlotsReference
    lateinit var slotsDbRef: DatabaseReference

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private val args: DoctorDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = binding.doctorDetailFragmentToolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        setHasOptionsMenu(true)

        val doctorDetails = args.selectedDoctor

        binding.doctorName.text = doctorDetails.name
        binding.doctorHospitalLocation.text = buildSpannedString {
            bold { append("Hospital: ") }
            append(doctorDetails.location)
        }
        binding.doctorSpecialization.text = "Specialization: ${doctorDetails.category}"
        binding.doctorQualification.text = "Qualification: ${doctorDetails.qualification}"
        binding.doctorExperience.text = "Experience: ${doctorDetails.experience}"
        binding.doctorMailID.text = "Email Id: ${doctorDetails.mailId}"
        binding.doctorContactNumber.text = "Contact No.: ${doctorDetails.contact}"

        binding.availableSlotsButton.setOnClickListener {
            showCalendarDialog()
        }
    }

//    private fun showCalendarDialog() {
//        val binding = DialogCalendarBinding.inflate(LayoutInflater.from(requireContext()))
//        val dialog = Dialog(requireContext())
//        dialog.setContentView(binding.root)
//
//        val calendarView: CalendarView = binding.calendarView
//
//        var events: MutableMap<String, String> = mutableMapOf()
//        val calendars: ArrayList<CalendarDay> = ArrayList()
//        val calendar = Calendar.getInstance()
//
//        calendar.set(2025, 2, 19)
//        val calendarDay = CalendarDay(calendar)
//        calendarDay.labelColor = R.color.lavender
//        calendarDay.imageResource = R.drawable.baseline_event_available_24
//        calendars.add(calendarDay)
//        events["19-02-2025"] = "Appointment Slot"
//
//        calendarView.setCalendarDays(calendars)
//
//        calendarView.setOnCalendarDayClickListener(object : OnCalendarDayClickListener {
//            override fun onClick(calendarDay: CalendarDay) {
//                val clickedDate = calendarDay.calendar
//                val selectedDate =
//                    "${clickedDate.get(Calendar.DAY_OF_MONTH)}-${clickedDate.get(Calendar.MONTH) + 1}-${
//                        clickedDate.get(Calendar.YEAR)
//                    }"
//
//                if(events.containsKey(selectedDate)) {
//                    dialog.dismiss()
//                    showBottomSheetDialog(selectedDate)
//                } else {
//                    showToast("No Appointments Available!")
//                }
//            }
//        })
//
//        dialog.show()
//    }

    private fun showCalendarDialog() {
        val binding = DialogCalendarBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = Dialog(requireContext())
        dialog.setContentView(binding.root)

        val calendarView: CalendarView = binding.calendarView

        calendarView.setOnCalendarDayClickListener(object : OnCalendarDayClickListener {
            override fun onClick(calendarDay: CalendarDay) {
                val clickedDate = calendarDay.calendar
                val selectedDate =
                    "${clickedDate.get(Calendar.DAY_OF_MONTH)}/${clickedDate.get(Calendar.MONTH) + 1}/${
                        clickedDate.get(Calendar.YEAR)
                    }"

                dialog.dismiss()
                showBottomSheetDialog(selectedDate)
            }
        })

        dialog.show()
    }

    private fun showBottomSheetDialog(selectedDate: String) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())

        val binding = BottomSheetDialogBinding.inflate(LayoutInflater.from(requireContext()))

        binding.selectedDateText.text = "Selected Date: $selectedDate"

        binding.morningSlotSelectIcon.setOnClickListener {
            val selectedSlot = binding.morningSlotTitle.text.toString().trim()
            saveSlotToDatabase(selectedDate, selectedSlot, "Morning")
            bottomSheetDialog.dismiss()
        }

        binding.afternoonSlotIcon.setOnClickListener {
            val selectedSlot = binding.afternoonSlotTitle.text.toString().trim()
            saveSlotToDatabase(selectedDate, selectedSlot, "Afternoon")
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(binding.root)
        bottomSheetDialog.show()
    }

    private fun saveSlotToDatabase(
        selectedDate: String,
        selectedSlot: String,
        slotDuration: String
    ) {
        val id = UUID.randomUUID().toString()
        val doctorID = args.selectedDoctor.id
        val doctorName = args.selectedDoctor.name
        val doctorSpecialization = args.selectedDoctor.category
        val hospitalName = args.selectedDoctor.location

        val formattedDate = selectedDate.replace("/", "-")
        val uniqueID = "${doctorID}_${formattedDate}_${slotDuration}"
        val userID = firebaseAuth.currentUser?.uid

        val slotData =
            BookedSlot(
                id,
                selectedDate,
                selectedSlot,
                doctorName,
                doctorSpecialization,
                hospitalName
            )

        if (userID != null) {
            val slotRef = slotsDbRef.child(userID).child(uniqueID)
            slotRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    showToast("You have already booked this slot on $selectedDate!")
                } else {
                    slotsDbRef.child(userID).child(uniqueID).setValue(slotData)
                        .addOnSuccessListener {
                            showToast("Appointment booked successfully!")
                            findNavController().navigate(R.id.action_doctorDetailFragment_to_bookedSlotsFragment)
                        }.addOnFailureListener {
                            showToast("Appointment booking failed!")
                        }
                }
            }.addOnFailureListener {
                showToast("Error checking existing slots!")
            }
        } else {
            showToast("User session expired or not logged in!")
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
