package com.example.android.navigation

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
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

class EditFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var placeNameEditText: EditText
    private lateinit var datePickerButton: Button
    private lateinit var spinner: Spinner
    private lateinit var seekBar: SeekBar
    private lateinit var notesEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var bar: SeekBar
    private var selectedImageUri: Uri? = null // Added for image selection
    private val IMAGE_PICK_REQUEST_CODE = 1001 // Added for image selection
    private var pressed : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_memory, container, false)
        val destination = arguments?.getParcelable<Destination>("destination")
        val user = arguments?.getParcelable<User>("user")
        val destinationId = destination?.id
        placeNameEditText = view.findViewById(R.id.PlaceName)
        datePickerButton = view.findViewById(R.id.buttonDatePicker)
        spinner = view.findViewById(R.id.spinner)
        seekBar = view.findViewById(R.id.seekBar)
        notesEditText = view.findViewById(R.id.Notes)
        saveButton = view.findViewById(R.id.registerButton)
        bar = view.findViewById(R.id.seekBar)
        val selectImageButton = view.findViewById<Button>(R.id.selectImageButton) // Added for image selection
        if(destination == null){
            val toastMessage = "input is null"
            Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()
        }
        if(user == null) {
            val toastMessage = "user is null"
            Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()
        }
        val placeName = destination?.place
        placeNameEditText.text = Editable.Factory.getInstance().newEditable(placeName)
        val actualDate = destination?.date
        datePickerButton.text = Editable.Factory.getInstance().newEditable(actualDate)


        val languageArray = resources.getStringArray(R.array.languages)
        val spinnerAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, languageArray)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
        var poz : Int = 0
        if (destination?.travelType != null && spinnerAdapter != null) {

            for (position in 0 until languageArray.size) {
                val item = languageArray[position]
                if (destination.travelType == item) {
                    poz = position
                    spinner.setSelection(poz, false)
                    break
                }
            }
        }


        val mood = destination?.mood ?: 0 // Set a default value in case mood is null
        seekBar.progress = mood


        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.languages,
            R.layout.custom_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.setSelection(poz, false)
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
        saveButton.setOnClickListener {
            val placeName = placeNameEditText.text.toString()
            val dateOfTravel = datePickerButton.text.toString()

            if (placeName.isBlank()) {
                Toast.makeText(requireContext(), "Please enter a name", Toast.LENGTH_SHORT).show()
            } else if (dateOfTravel == "Select Date") {
                Toast.makeText(requireContext(), "Please select a date", Toast.LENGTH_SHORT).show()
            } else {
                if (destination != null) {
                    editMemory(user, destinationId, destination)
                }
                val action = EditFragmentDirections.actionEditFragmentToGameFragment(user!!)
                view.findNavController().navigate(action)
            }
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            pressed = 1
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



    private fun editMemory(user: User?, id : Int?, destination: Destination) {
        val placeName = placeNameEditText.text.toString()
        val dateOfTravel = datePickerButton.text.toString()
        val selectedLanguage = spinner.selectedItem.toString()
        val excitementLevel = seekBar.progress
        val notes = notesEditText.text.toString()

        if (selectedLanguage == getString(R.string.select_language_hint)) {
            Toast.makeText(requireContext(), "Please select a type of transport", Toast.LENGTH_SHORT).show()
            return  // Don't save memory if the first item is selected
        }

        val updatedDestination = getImageResourceIdFromUri(selectedImageUri)?.let {
            Destination(
                place = placeName,
                date = dateOfTravel,
                travelType = selectedLanguage,
                notes = notes,
                mood = excitementLevel,
                imageResourceId = it // Replace with the actual image resource ID
            )
        }
        if(pressed == 0) {
            updatedDestination?.imageResourceId = destination.imageResourceId
        }
        user?.let {
            val updatedDestinations = it.destinations?.toMutableList()
            val destinationIndex = updatedDestinations?.indexOfFirst { destination -> destination.id == id }  // Replace `destinationId` with the actual ID of the destination being edited
            if (destinationIndex != null && destinationIndex >= 0) {
                if (updatedDestination != null) {
                    updatedDestinations[destinationIndex] = updatedDestination
                }
            }

            it.destinations = updatedDestinations?.toList()

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
