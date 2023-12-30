package com.example.traveldiary.ui.view_destination

import android.content.Context
import android.content.pm.PackageManager

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.traveldiary.R
import com.example.traveldiary.database.Destination
import com.example.traveldiary.database.User
import com.example.traveldiary.databinding.FragmentViewDestinationBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ViewDestinationFragment : Fragment() {

    private var _binding: FragmentViewDestinationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(ViewDestinationViewModel::class.java)

        _binding = FragmentViewDestinationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPreferences = requireContext().getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val destinationJson = sharedPreferences.getString("currentDestination", "")
        val gson = Gson()
        val destination: Destination = gson.fromJson(destinationJson, Destination::class.java)
        if (destination.name.isEmpty()) {
            binding.destinationName.visibility = View.GONE
        } else {
            binding.destinationName.text = destination.name
            binding.destinationName.visibility = View.VISIBLE
        }
        if (destination.date.isEmpty()) {
            binding.destinationDate.visibility = View.GONE
        } else {
            binding.destinationDate.text = getString(R.string.date) + ": " + "${destination.date}"
            binding.destinationDate.visibility = View.VISIBLE
        }
        if (destination.type == "") {
            binding.destinationType.visibility = View.GONE
        } else {
            binding.destinationType.text = getString(R.string.typeOfTransport) +": " + "${destination.type}"
            binding.destinationType.visibility = View.VISIBLE
        }
        if (destination.description.isEmpty()) {
            binding.destinationDescription.visibility = View.GONE
        } else {
            binding.destinationDescription.text = getString(R.string.description) + ": " + "${destination.description}"
            binding.destinationDescription.visibility = View.VISIBLE
        }
        if (destination.location == null) {
            binding.destinationDescription.visibility = View.GONE
        } else {
            if(destination.location.name.isEmpty()) {
                binding.destinationLocation.visibility = View.GONE
            } else {
                binding.destinationLocation.text = getString(R.string.place) + ": ${destination.location.name}\n" + getString(R.string.coordonates) + ": ${String.format("%.3f", destination.location.latitudine)}, " +
                                "${String.format("%.3f", destination.location.longitudine)}\n" + getString(R.string.weather) + ": ${destination.location.weather}\n" +
                        getString(R.string.temperature) + ": " + "${destination.location.temperature}\n" + getString(R.string.humidity) +": " + "${destination.location.humidity}"
                binding.destinationLocation.visibility = View.VISIBLE
            }
        }
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Glide.with(this)
                .load(destination.photo)
                .apply(RequestOptions().fitCenter()) // Use fitCenter to maintain aspect ratio
                .into(binding.destinationImage)
        } else {
            Glide.with(this)
                .load(destination.photo)
                .apply(RequestOptions().fitCenter()) // Use fitCenter to maintain aspect ratio
                .into(binding.destinationImage)
        }
        binding.deleteButton.setOnClickListener{
            val currentUserJson = sharedPreferences.getString("currentUser", "")
            val currentUser: User? = gson.fromJson(currentUserJson, User::class.java)
            val builder = AlertDialog.Builder(requireContext())

            // Set the dialog title and message
            builder.setTitle(R.string.sureQuestion)
                .setMessage(R.string.deleteDestinationQuestion)

            // Add "Yes" button
            builder.setPositiveButton(R.string.yes) { _, _ ->
                // User clicked "Yes," navigate to the login screen
                if (currentUser != null) {
                    // Specify the name and date of the destination you want to delete
                    val destinationNameToDelete = binding.destinationName.text
                    val destinationDateToDelete = binding.destinationDate.text

                    // Find the destination with the specified name and date, and remove it
                    val updatedDestinations = currentUser.destinationList.filterNot { destination ->
                        destination.name == destinationNameToDelete && destination.date == destinationDateToDelete
                    }

                    // Update the 'currentUser' object with the filtered destinations
                    currentUser.destinationList = updatedDestinations as ArrayList<Destination>

                    val userListJson = sharedPreferences.getString("userList", null)
                    val editor = sharedPreferences.edit()
                    val userListType = object : TypeToken<List<User>>() {}.type
                    var userList = gson.fromJson<List<User>>(userListJson, userListType)
                    if (userList == null) {
                        userList = ArrayList()
                    }

                    // Find the current user in the user array and update it
                    userList = userList.map { if (it.username == currentUser.username) currentUser else it }

                    // Convert the updated user array to JSON and save it back to SharedPreferences
                    val updatedUserListJson = gson.toJson(userList)
                    editor.putString("userList", updatedUserListJson)

                    // Serialize the updated 'currentUser' object back to JSON
                    val updatedCurrentUserJson = gson.toJson(currentUser)

                    // Save the updated 'currentUser' object back to SharedPreferences
                    editor.putString("currentUser", updatedCurrentUserJson).apply()
                    editor.apply()

                    // Optionally, you can notify the user that the destination has been deleted
                    Toast.makeText(requireContext(), R.string.destinationDeleted, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.nav_home)
                }
            }
            // Add "No" button
            builder.setNegativeButton(R.string.no) { _, _ ->
                // User clicked "No," do nothing or handle it as needed
                // For example, you can dismiss the dialog or perform another action
            }

            // Create and show the AlertDialog
            val dialog = builder.create()
            dialog.show()
        }

        binding.editButton.setOnClickListener{
            findNavController().navigate(R.id.nav_edit_destination)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}