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
import com.example.telemedicineapp.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = binding.loginToolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
        }
        setHasOptionsMenu(true)

        binding.loginButton.setOnClickListener {
            val mailID = binding.mailIDInputField.text.toString().trim()
            val password = binding.passwordInputField.text.toString().trim()

            when {
                mailID.isEmpty() -> {
                    binding.mailIDInputField.error = "Please enter the mail ID"
                    binding.mailIDInputField.requestFocus()
                }

                !android.util.Patterns.EMAIL_ADDRESS.matcher(mailID).matches() -> {
                    binding.mailIDInputField.error = "Invalid email format"
                    binding.mailIDInputField.requestFocus()
                }

                password.isEmpty() -> {
                    binding.passwordInputField.error = "Please enter the password"
                    binding.passwordInputField.requestFocus()
                }

                else -> {
                    loginUser(mailID, password)
                }
            }

        }

        binding.forgotPasswordLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_resetPasswordFragment)
        }

        binding.registerLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

    }

    private fun loginUser(mailID: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(mailID, password).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(requireContext(), "Signed in Successfully", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            } else {
                //Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_SHORT).show()
                Toast.makeText(requireContext(), "Failed to Sign In", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (firebaseAuth.currentUser != null) {
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}