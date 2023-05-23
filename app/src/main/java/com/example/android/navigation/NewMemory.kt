package com.example.android.navigation

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.android.navigation.database.Destination
import com.example.android.navigation.database.User
import com.example.android.navigation.database.UserDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NewMemory : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var placeNameEditText: EditText
    private lateinit var datePickerButton: Button
    private lateinit var spinner: Spinner
    private lateinit var seekBar: SeekBar
    private lateinit var notesEditText: EditText
    private lateinit var saveButton: Button
    private var selectedImageUri: Uri? = null // Added for image selection
    private val IMAGE_PICK_REQUEST_CODE = 1001 // Added for image selection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_memory, container, false)
        val user = arguments?.getParcelable<User>("user")
        placeNameEditText = view.findViewById(R.id.PlaceName)
        datePickerButton = view.findViewById(R.id.buttonDatePicker)
        spinner = view.findViewById(R.id.spinner)
        seekBar = view.findViewById(R.id.seekBar)
        notesEditText = view.findViewById(R.id.Notes)
        saveButton = view.findViewById(R.id.registerButton)
        val selectImageButton = view.findViewById<Button>(R.id.selectImageButton) // Added for image selection

        if (user != null) {
            saveButton.setOnClickListener {
                val placeName = placeNameEditText.text.toString()
                val dateOfTravel = datePickerButton.text.toString()

                if (placeName.isBlank()) {
                    Toast.makeText(requireContext(), "Please enter a name", Toast.LENGTH_SHORT).show()
                } else if (dateOfTravel == "Select Date") {
                    Toast.makeText(requireContext(), "Please select a date", Toast.LENGTH_SHORT).show()
                } else {
                    saveMemory(user)
                    val action = NewMemoryDirections.actionNewMemoryToGameFragment(user)
                    view.findNavController().navigate(action)
                }
            }
        } else {
            Toast.makeText(requireContext(), "User null!", Toast.LENGTH_SHORT).show()
        }

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.languages,
            R.layout.custom_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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
                if (position == 0) {
                    // The first item (hint) is selected
                } else {
                    val selectedLanguage = parent.getItemAtPosition(position) as String
                    // Handle the selected language
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case where nothing is selected
            }
        }

        val calendar = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
            val selectedDate = dateFormat.format(calendar.time)

            datePickerButton.text = selectedDate
        }

        datePickerButton.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        selectImageButton.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, IMAGE_PICK_REQUEST_CODE)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            val imageResourceId = getImageResourceIdFromUri(selectedImageUri)
            // Perform any additional logic with the selected image URI and resource ID
        }
    }
    private fun getImageResourceIdFromUri(uri: Uri?): Int? {
        uri?.let {
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val columnNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (columnNameIndex != -1 && it.moveToFirst()) {
                    val imageName = it.getString(columnNameIndex)
                    return getDrawableResourceId(removeTextAfterSpaceOrPeriod(imageName))
                }
            }
        }

        return R.drawable.no_image_placeholder_svg
    }
    fun getDrawableResourceId(resourceName: String): Int? {
        val resources = context?.resources
        if (resources != null) {
            return resources.getIdentifier(resourceName, "drawable", context?.packageName ?: "travel")
        }
        return null;
    }
    private fun removeTextAfterSpaceOrPeriod(input: String): String {
        val indexOfSpaceOrPeriod = input.indexOfAny(charArrayOf(' ', '.'))
        return if (indexOfSpaceOrPeriod != -1) {
            input.substring(0, indexOfSpaceOrPeriod)
        } else {
            input
        }
    }



    private fun saveMemory(user: User?) {
        val placeName = placeNameEditText.text.toString()
        val dateOfTravel = datePickerButton.text.toString()
        val selectedLanguage = spinner.selectedItem.toString()
        val excitementLevel = seekBar.progress
        val notes = notesEditText.text.toString()

        if (selectedLanguage == getString(R.string.select_language_hint)) {
            Toast.makeText(requireContext(), "Please select a type of transport", Toast.LENGTH_SHORT).show()
            return  // Don't save memory if the first item is selected
        }

        val newMemory = getImageResourceIdFromUri(selectedImageUri)?.let {
            Destination(
                place = placeName,
                date = dateOfTravel,
                travelType = selectedLanguage,
                notes = notes,
                mood = excitementLevel,
                imageResourceId = it // Replace with the actual image resource ID
            )
        }

        user?.let {
            val updatedDestinations = it.destinations?.toMutableList()
            if (newMemory != null) {
                updatedDestinations?.add(newMemory)
            }
            it.destinations = updatedDestinations?.toList()
            val destinationsSize = newMemory?.date

            val userDao = UserDatabase.getInstance(requireContext()).userDao
            CoroutineScope(Dispatchers.IO).launch {
                userDao.updateUser(user)
            }
        }
    }

    data class Memory(
        val placeName: String,
        val dateOfTravel: String,
        val selectedLanguage: String,
        val excitementLevel: Int,
        val notes: String
    )

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewMemory().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
