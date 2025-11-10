package com.example.telemedicineapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.telemedicineapp.R
import com.example.telemedicineapp.databinding.FragmentUserProfileBinding
import com.example.telemedicineapp.di.SlotsReference
import com.example.telemedicineapp.di.UsersReference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    @UsersReference
    lateinit var userDbRef: DatabaseReference

    @Inject
    @SlotsReference
    lateinit var slotsDbRef: DatabaseReference

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var userId: String

    private var fullName: String? = null
    private var dob: String? = null
    private var gender: String? = null
    private var mailID: String? = null
    private var phone: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = binding.userProfileFragmentToolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        setHasOptionsMenu(true)

        fetchUserDataFromFirebase()

        binding.deleteAccountButton.setOnClickListener {
            deleteUserAccountFromFirebase()
        }

        binding.editDetailsButton.setOnClickListener {
            if (fullName != null && dob != null && phone != null) {
                editUserDataOnFirebase()
            } else {
                showToastMessage("Please wait until the data is fetched")
            }
        }
    }

    private fun fetchUserDataFromFirebase() {
        userId = firebaseAuth.currentUser?.uid ?: ""
        userDbRef.child(userId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                fullName = snapshot.child("fullName").value?.toString() ?: "N/A"
                dob = snapshot.child("dob").value?.toString() ?: "N/A"
                gender = snapshot.child("gender").value?.toString() ?: "N/A"
                mailID = snapshot.child("mailID").value?.toString() ?: "N/A"
                phone = snapshot.child("phoneNumber").value?.toString() ?: "N/A"

                binding.userName.text = fullName
                binding.userDOB.text = "DOB: $dob"
                binding.userGender.text = "Gender: $gender"
                binding.userMailID.text = "MailId: $mailID"
                binding.userContactNumber.text = "Contact Number: $phone"
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("InflateParams")
    private fun editUserDataOnFirebase() {
        val inflater = LayoutInflater.from(requireContext())

        val bindingDialog =
            com.example.telemedicineapp.databinding.DialogEditUserDetailsBinding.inflate(
                inflater,
                null,
                false
            )

        bindingDialog.edtFullName.setText(fullName)
        bindingDialog.edtDob.setText(dob)
        bindingDialog.edtContactNumber.setText(phone)

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Edit User Details")
            .setView(bindingDialog.root)
            .setPositiveButton("Save") { _, _ ->
                val updatedFullName = bindingDialog.edtFullName.text.toString()
                val updatedDob = bindingDialog.edtDob.text.toString()
                val updatedContactNumber = bindingDialog.edtContactNumber.text.toString()

                if (updatedFullName.isNotEmpty() && updatedDob.isNotEmpty() && updatedContactNumber.isNotEmpty()) {
                    val updatedUserData = mapOf(
                        "fullName" to updatedFullName,
                        "dob" to updatedDob,
                        "phoneNumber" to updatedContactNumber
                    )

                    userDbRef.child(userId).updateChildren(updatedUserData).addOnSuccessListener {
                        fullName = updatedFullName
                        dob = updatedDob
                        phone = updatedContactNumber

                        binding.userName.text = fullName
                        binding.userDOB.text = "DOB: $dob"
                        binding.userContactNumber.text = "Contact Number: $phone"

                        showToastMessage("Details updated successfully")
                    }.addOnFailureListener {
                        showToastMessage("Failed to update details")
                    }
                } else {
                    showToastMessage("Please fill in all fields")
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun deleteUserAccountFromFirebase() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                userDbRef.child(userId).removeValue().addOnSuccessListener {
                    slotsDbRef.child(userId).removeValue().addOnSuccessListener {
                        firebaseAuth.currentUser?.delete()?.addOnCompleteListener { authTask ->
                            if (authTask.isSuccessful) {
                                firebaseAuth.signOut()
                                showToastMessage("Account deleted successfully")
                                findNavController().navigate(R.id.action_userProfileFragment_to_loginFragment)
                            } else {
                                showToastMessage("Error deleting user account from Firebase Authentication")
                            }
                        }
                    }.addOnFailureListener {
                        showToastMessage("Error deleting user slots")
                    }
                }.addOnFailureListener {
                    showToastMessage("Error deleting user data")
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        val alert = builder.create()
        alert.show()
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
