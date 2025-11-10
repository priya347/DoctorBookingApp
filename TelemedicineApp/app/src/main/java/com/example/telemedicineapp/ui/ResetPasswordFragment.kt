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
import com.example.telemedicineapp.databinding.FragmentResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = binding.resetPasswordToolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        setHasOptionsMenu(true)

        binding.resetPasswordButton.setOnClickListener {
            val mailID = binding.mailIDField.text.toString().trim()

            when {
                mailID.isEmpty() -> {
                    binding.mailIDField.error = "Please enter the mail ID"
                    binding.mailIDField.requestFocus()
                }

                !android.util.Patterns.EMAIL_ADDRESS.matcher(mailID).matches() -> {
                    binding.mailIDField.error = "Invalid email format"
                    binding.mailIDField.requestFocus()
                }

                else -> {
                    resetPassword(mailID)
                }
            }

        }

    }

    private fun resetPassword(mailID: String) {
        firebaseAuth.sendPasswordResetEmail(mailID).addOnSuccessListener {
            Toast.makeText(
                requireContext(),
                "Reset Password link has been sent to the registered email",
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigate(R.id.action_resetPasswordFragment_to_loginFragment)
        }.addOnFailureListener {
            Toast.makeText(
                requireContext(),
                "Failed to send Reset Password link",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}