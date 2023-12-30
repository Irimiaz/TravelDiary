package com.example.traveldiary.ui.edit_destination

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.example.traveldiary.R
import com.example.traveldiary.database.Destination
import com.example.traveldiary.database.Location
import com.example.traveldiary.database.User
import com.example.traveldiary.databinding.FragmentAboutUsBinding
import com.example.traveldiary.databinding.FragmentEditDestinationBinding
import com.example.traveldiary.ui.about_us.AboutUsViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class EditDestinationFragment : Fragment() {

    private var _binding: FragmentEditDestinationBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var photo : String = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(EditDestinationViewModel::class.java)

        _binding = FragmentEditDestinationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val saveButton : Button = binding.saveButton
        val calendar = Calendar.getInstance()
        val sharedPref = requireActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val destinationJson = sharedPref.getString("currentDestination", "")
        val gson = Gson()
        val prevDestination: Destination = gson.fromJson(destinationJson, Destination::class.java)
        // Retrieve the current user from the user array
        val currentUserJson = sharedPref.getString("currentUser", null)
        var currentUser = gson.fromJson(currentUserJson, User::class.java)

        val typesOfTransportArray = resources.getStringArray(R.array.typesOfTransport)
        val searchString = prevDestination.type // Replace with the string you want to find
        var position = -1
        for (i in typesOfTransportArray.indices) {
            if (typesOfTransportArray[i] == searchString) {
                position = i
                break // Exit the loop once the string is found
            }
        }

        binding.PlaceName.setText(prevDestination.name, TextView.BufferType.EDITABLE);
        binding.destinationDate.setText(prevDestination.date, TextView.BufferType.EDITABLE)
        binding.Description.setText(prevDestination.description, TextView.BufferType.EDITABLE)
        //binding.destinationType.getItemAtPosition(2).toString()
        val destinationTypeSpinner = root.findViewById<Spinner>(R.id.destinationType)
        val adapter2 = ArrayAdapter.createFromResource(requireContext(), R.array.typesOfTransport, android.R.layout.simple_spinner_item)
        adapter2.setDropDownViewResource(R.layout.custom_spinner_item)
        destinationTypeSpinner.adapter = adapter2 // Set the adapter first
        destinationTypeSpinner.setSelection(2, false) // Then set the selected item


        binding.destinationDate.setOnClickListener {
            // Create a DatePickerDialog to pick a date
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    // This callback is called when a date is selected in the dialog
                    val selectedDate = "$dayOfMonth-${monthOfYear + 1}-$year" // Format the selected date
                    binding.destinationDate.text = selectedDate // Set the selected date to your EditText
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            // Show the DatePickerDialog
            datePickerDialog.show()
        }
        binding.destinationPhoto.setOnClickListener {
            val intent : Intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 3)

        }
        var type : String = ""
        val spinner = root.findViewById<Spinner>(R.id.destinationType)
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.typesOfTransport,
            R.layout.custom_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.custom_spinner_item)
            spinner.adapter = adapter
            spinner.setSelection(0, false)
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                type = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(requireContext(), "nothing selected", Toast.LENGTH_SHORT).show()
            }
        }
        binding.destinationLocation.setOnClickListener {
            findNavController().navigate(R.id.nav_location)
        }
        saveButton.setOnClickListener {
            var name: String = binding.PlaceName.text.toString()
            var description: String = binding.Description.text.toString()
            var date: String = binding.destinationDate.text.toString()
            val sharedPref = requireActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            val destinationJson = sharedPref.getString("currentDestination", "")
            val gson = Gson()
            val prevDestination: Destination = gson.fromJson(destinationJson, Destination::class.java)



            // Retrieve the current user from the user array
            val currentUserJson = sharedPref.getString("currentUser", null)
            var currentUser = gson.fromJson(currentUserJson, User::class.java)
            val currentLocationJson = sharedPref.getString("currentLocation", null)
            var currentLocation = gson.fromJson(currentLocationJson, Location::class.java)
            var location : Location
            var finalLocation : Location? = null
            if(currentLocationJson == null) {
                location = Location(prevDestination.location.name, prevDestination.location.latitudine, prevDestination.location.longitudine, prevDestination.location.weather,prevDestination.location.temperature,prevDestination.location.humidity)
            } else {
                location = Location(currentLocation.name, currentLocation.latitudine, currentLocation.longitudine,"","","")
                runWeather(location) { updatedLocation ->
                    finalLocation = updatedLocation
                }
            }

            // Create a new Destination
            if(photo.equals("")) {
                val packageName : String = "com.example.traveldiary"
                // If the user didn't select an image, assign a default photo resource
                val defaultPhotoUri = Uri.parse("android.resource://$packageName/${R.drawable.no_image_placeholder_svg}")
                photo = defaultPhotoUri.toString()
            }
            if(type.equals("")) type = prevDestination.type
            if(photo.equals("android.resource://com.example.traveldiary/2131230963")) photo = prevDestination.photo
            var  newDestination : Destination
            if (finalLocation == null)
                newDestination = Destination(name, description,date, photo, type, location)
            else newDestination = Destination(name, description,date, photo, type, finalLocation!!)

            // Modify the current user's destination list
            if (currentUser.destinationList == null) {
                currentUser.destinationList = ArrayList()
            }
            val existingDestinationIndex = currentUser.destinationList?.indexOfFirst {
                it.name == prevDestination.name && it.date == prevDestination.date
            }
            if (existingDestinationIndex != -1) {
                // Update the existing destination with the new one
                if (existingDestinationIndex != null) {
                    currentUser.destinationList?.set(existingDestinationIndex, newDestination)
                }
            } else {
                // Add the new destination if it doesn't already exist
                currentUser.destinationList?.add(newDestination)
            }

            // Retrieve the user list from shared preferences
            val userListJson = sharedPref.getString("userList", null)
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

            // Convert the modified current user to JSON and save it back to SharedPreferences
            val updatedCurrentUserJson = gson.toJson(currentUser)
            editor.putString("currentUser", updatedCurrentUserJson)

            // Apply the changes to SharedPreferences
            editor.apply()

            // Navigate to the desired destination
            findNavController().navigate(R.id.nav_home)
        }
        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            // User selected an image, get its URI
            val selectedImageUri = data.data

            // Now you can use the selectedImageUri to store or display the selected image
            photo = selectedImageUri.toString()
            Toast.makeText(requireContext(), R.string.photoSaved , Toast.LENGTH_SHORT).show()
        }
    }

    private fun runWeather(location: Location, callback: (Location) -> Unit) {
        val APIKey = "06e89098889b09aba8ad608208ec482f"
        val weatherUrl: String =
            "https://api.openweathermap.org/data/2.5/weather?lat=${location.latitudine}&lon=${location.longitudine}&appid=$APIKey&units=metric"

        // Use coroutines to perform the network operation in the background
        runBlocking {
            launch(Dispatchers.IO) {
                try {
                    val resultJson = URL(weatherUrl).readText()
                    //Log.d("Weather Report", resultJson)
                    val jsonObj = JSONObject(resultJson)
                    val main = jsonObj.getJSONObject("main")
                    val weather = jsonObj.getJSONArray("weather")
                    val condition = weather.getJSONObject(0).getString("main")
                    val temperature = main.getString("temp") + "°C"
                    val humidity = main.getString("humidity") + "%"
                    val out = "$condition $temperature $humidity"
                    //Log.d("Out", out)
                    val newLocation = Location(location.name, location.latitudine, location.longitudine, condition, temperature, humidity)
                    callback(newLocation)
                } catch (e: Exception) {
                    Log.e("Weather Error", "Error fetching weather data: ${e.message}", e)
                    callback(location)
                    // Handle the error
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}