package com.example.traveldiary.ui.home

import android.R
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.traveldiary.database.Destination
import com.example.traveldiary.database.User
import com.example.traveldiary.databinding.FragmentHomeBinding
import com.example.traveldiary.ui.recycler.MyAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class HomeFragment : Fragment() {

private var _binding: FragmentHomeBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    val root: View = binding.root
    val preferences = requireContext().getSharedPreferences("myPref", 0)
    preferences.edit().remove("currentDestination").commit()
    preferences.edit().remove("currentLocation").commit()
    // Set up the FloatingActionButton
    val addNewDestination: Button = binding.fab
    addNewDestination.setOnClickListener { view ->
      findNavController().navigate(com.example.traveldiary.R.id.nav_new_destination)
    }

    // Set up the TextView
    val textView: TextView = binding.textHome

    val sharedPreferences = requireContext().getSharedPreferences("myPref", Context.MODE_PRIVATE)
    val userJson = sharedPreferences.getString("currentUser", "")
    val gson = Gson()

    try {
      val user: User = gson.fromJson(userJson, User::class.java)

      // Now, you can access the destinationList from the User object
      val destinations: List<Destination> = user.destinationList

      // Set up the RecyclerView with a LinearLayoutManager
      val recyclerView: RecyclerView = binding.recyclerView
      val layoutManager = LinearLayoutManager(requireContext())
      recyclerView.layoutManager = layoutManager

      // Set the adapter
      val adapter = MyAdapter(requireContext(), destinations) { clickedDestination ->
        // Handle the item click here using the entire Destination object

        // Save the clicked Destination to SharedPreferences
        val sharedPreferencesEditor = sharedPreferences.edit()
        val gson = Gson()
        val destinationJson = gson.toJson(clickedDestination)
        sharedPreferencesEditor.putString("currentDestination", destinationJson)
        sharedPreferencesEditor.apply()
        findNavController().navigate(com.example.traveldiary.R.id.nav_view_destination)
      }

      recyclerView.adapter = adapter

      if (destinations.isEmpty()) {
        // Handle the case when there are no destinations
        textView.text = getString(com.example.traveldiary.R.string.noDestinations)
      } else {
        // Set a message indicating the number of destinations
        textView.visibility = View.GONE
        textView.text = "You have ${destinations.size} destinations."
      }
    } catch (e: Exception) {
      // Handle JSON parsing errors or any other exceptions here
      textView.text = "Error loading destinations."
      e.printStackTrace()
    }

    return root
  }


  override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}