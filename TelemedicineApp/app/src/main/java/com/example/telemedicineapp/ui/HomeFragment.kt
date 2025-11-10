package com.example.telemedicineapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.telemedicineapp.R
import com.example.telemedicineapp.adapter.DoctorAdapter
import com.example.telemedicineapp.client.RetrofitClient
import com.example.telemedicineapp.databinding.FragmentHomeBinding
import com.example.telemedicineapp.model.Doctor
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchView: SearchView
    private lateinit var doctorsListRecyclerView: RecyclerView
    private lateinit var doctorsAdapter: DoctorAdapter
    private var doctorsList: List<Doctor> = listOf()

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = binding.homeToolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
        }
        setHasOptionsMenu(true)

        binding.bookedSlotsButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_bookedSlotsFragment)
        }

        doctorsListRecyclerView = binding.doctorsListRecyclerView
        doctorsListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        doctorsAdapter = DoctorAdapter(mutableListOf()) { selectedDoctor ->
            val action =
                HomeFragmentDirections.actionHomeFragmentToDoctorDetailFragment(selectedDoctor)
            findNavController().navigate(action)
        }
        binding.doctorsListRecyclerView.adapter = doctorsAdapter

        fetchDoctorsList()

        searchView = binding.doctorSearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterDoctorsList(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterDoctorsList(it) }
                return true
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (findNavController().currentDestination?.id == R.id.homeFragment) {
                requireActivity().finish()
            } else {
                findNavController().navigateUp()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.home_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logoutButton -> {
                firebaseAuth.signOut()
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                true
            }

            R.id.userProfileButton -> {
                findNavController().navigate(R.id.action_homeFragment_to_userProfileFragment)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun fetchDoctorsList() {
        RetrofitClient.getDoctorsList().enqueue(object : Callback<List<Doctor>> {
            override fun onResponse(call: Call<List<Doctor>>, response: Response<List<Doctor>>) {
                if (response.isSuccessful && response.body() != null) {
                    doctorsList = response.body()!!
                    doctorsAdapter.updateList(doctorsList)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Doctor>>, t: Throwable) {
                Toast.makeText(requireContext(), "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterDoctorsList(query: String) {
        val filteredList = doctorsList.filter { doctor ->
            doctor.name.contains(query, ignoreCase = true) || doctor.category.contains(query, ignoreCase = true)
        }
        doctorsAdapter.updateList(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


