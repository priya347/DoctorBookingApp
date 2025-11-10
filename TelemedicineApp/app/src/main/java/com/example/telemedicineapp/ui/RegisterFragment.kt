package com.example.telemedicineapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.telemedicineapp.R
import com.example.telemedicineapp.databinding.FragmentRegisterBinding
import com.example.telemedicineapp.di.UsersReference
import com.example.telemedicineapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    @Inject
    @UsersReference
    lateinit var userDbRef: DatabaseReference

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = binding.registerToolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        setHasOptionsMenu(true)

        binding.registerButton.setOnClickListener {
            val fullName = binding.fullNameRegisterField.text.toString().trim()
            val mailID = binding.mailIDRegisterField.text.toString().trim()
            val password = binding.passwordRegisterField.text.toString().trim()
            val phoneNumber = binding.phoneNumberRegisterField.text.toString().trim()
            val dob = binding.dobRegisterField.text.toString().trim()

            val selectedGenderId = binding.genderRadioGroup.checkedRadioButtonId
            val gender = when (selectedGenderId) {
                R.id.genderMale -> "Male"
                R.id.genderFemale -> "Female"
                R.id.genderOther -> "Other"
                R.id.genderPreferNotToSay -> "Prefer not to say"
                else -> ""
            }

            val termsChecked = binding.termsConditionsCheckBox.isChecked

            when {
                fullName.isEmpty() -> {
                    binding.fullNameRegisterField.error = "Please enter your full name"
                    binding.fullNameRegisterField.requestFocus()
                }

                mailID.isEmpty() -> {
                    binding.mailIDRegisterField.error = "Please enter the mail ID"
                    binding.mailIDRegisterField.requestFocus()
                }

                !android.util.Patterns.EMAIL_ADDRESS.matcher(mailID).matches() -> {
                    binding.mailIDRegisterField.error = "Invalid email format"
                    binding.mailIDRegisterField.requestFocus()
                }

                password.length < 6 -> {
                    binding.passwordRegisterField.error = "Password must be at least 6 characters"
                    binding.passwordRegisterField.requestFocus()
                }

                phoneNumber.isEmpty() -> {
                    binding.phoneNumberRegisterField.error = "Please enter your phone number"
                    binding.phoneNumberRegisterField.requestFocus()
                }

                !android.util.Patterns.PHONE.matcher(phoneNumber).matches() -> {
                    binding.phoneNumberRegisterField.error = "Invalid phone number format"
                    binding.phoneNumberRegisterField.requestFocus()
                }

                dob.isEmpty() -> {
                    binding.dobRegisterField.error = "Please enter your date of birth"
                    binding.dobRegisterField.requestFocus()
                }

                !dob.matches(Regex("\\d{2}/\\d{2}/\\d{4}")) -> {
                    binding.dobRegisterField.error = "Invalid date format. Use dd/mm/yyyy"
                    binding.dobRegisterField.requestFocus()
                }

                gender.isEmpty() -> {
                    Toast.makeText(
                        requireContext(),
                        "Please select your gender",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                !termsChecked -> {
                    Toast.makeText(
                        requireContext(),
                        "Please agree to the Terms & Conditions",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    registerUser(fullName, mailID, password, phoneNumber, dob, gender)
                }
            }

        }

        binding.loginLink.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

    }

    private fun registerUser(
        fullName: String,
        mailID: String,
        password: String,
        phoneNumber: String,
        dob: String,
        gender: String
    ) {
        firebaseAuth.createUserWithEmailAndPassword(mailID, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user?.let {
                        val userID = it.uid
                        val userData = User(fullName, mailID, phoneNumber, dob, gender)

                        userDbRef.child(userID).setValue(userData).addOnSuccessListener {
                            Toast.makeText(
                                requireContext(),
                                "User Registered Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        }.addOnFailureListener { e ->
                            Toast.makeText(
                                requireContext(),
                                "Failed to save user: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        task.exception?.message.toString(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}