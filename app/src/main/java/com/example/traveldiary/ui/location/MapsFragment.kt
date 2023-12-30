package com.example.traveldiary.ui.location

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.traveldiary.R
import com.example.traveldiary.database.Destination
import com.example.traveldiary.database.Location
import com.example.traveldiary.database.User
import com.example.traveldiary.databinding.FragmentMapsBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.Place.Field
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.gson.Gson

class MapsFragment : Fragment() {
    private var name: String = ""
    private var latitudine: Double = 0.0
    private var longitudine: Double = 0.0
    private val callback = OnMapReadyCallback { googleMap ->
        // Initialize the googleMap variable here
        this.googleMap = googleMap
        val sharedPref = requireActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val currentLocationJson = sharedPref.getString("currentDestination", null)
        val gson = Gson()
        var currentLocation = gson.fromJson(currentLocationJson, Location::class.java)
        val currentUserJson = sharedPref.getString("currentUser", null)
        val currentUser = gson.fromJson(currentUserJson, User::class.java)
        googleMap?.let { map ->
            // Check the current map type
            when (currentUser.settings.satellite) {
                true -> {
                    // Switch to SATELLITE view
                    map.mapType = GoogleMap.MAP_TYPE_SATELLITE
                }
                false -> {
                    // Switch back to NORMAL view
                    map.mapType = GoogleMap.MAP_TYPE_NORMAL
                }
            }
        }

        val currentDestinationJson = sharedPref.getString("currentDestination", null)
        var currentDestination = gson.fromJson(currentDestinationJson, Destination::class.java)
        if(currentDestinationJson != null && !currentDestination.location.name.equals("")) {
            val location : LatLng = LatLng(currentDestination.location.latitudine, currentDestination.location.longitudine)
            googleMap?.addMarker(MarkerOptions().position(location).title(currentDestination.location.name))
            googleMap?.moveCamera(CameraUpdateFactory.newLatLng(location))
        }
        // You can also customize the map settings here, if needed
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true

        // Initialize the Places API
        Places.initialize(requireContext(), "AIzaSyDAQk2oI8PEdrJWWYwVlcQvZSPdV74MPCs")
    }

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    // Declare the googleMap variable
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Check for Google Play services availability
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(requireContext())

        if (resultCode != ConnectionResult.SUCCESS) {
            // Google Play services are not available on this device
            // You can show a message to the user or take other actions
            Toast.makeText(requireContext(), "Google Play services not available", Toast.LENGTH_SHORT).show()
        } else {
            // Initialize the map fragment
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(callback)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = requireActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val currentLocationJson = sharedPref.getString("currentDestination", null)
        val gson = Gson()
        var currentLocation = gson.fromJson(currentLocationJson, Location::class.java)
        val currentUserJson = sharedPref.getString("currentUser", null)
        val currentUser = gson.fromJson(currentUserJson, User::class.java)
        googleMap?.let { map ->
            // Check the current map type
            when (currentUser.settings.satellite) {
                true -> {
                    // Switch to SATELLITE view
                    map.mapType = GoogleMap.MAP_TYPE_SATELLITE
                }
                false -> {
                    // Switch back to NORMAL view
                    map.mapType = GoogleMap.MAP_TYPE_NORMAL
                }
            }
        }

        // Initialize the Places API
        Places.initialize(requireContext(), "AIzaSyDAQk2oI8PEdrJWWYwVlcQvZSPdV74MPCs")

        // Set up autocomplete functionality
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?
        autocompleteFragment?.setPlaceFields(listOf(Field.ID, Field.NAME, Field.LAT_LNG))
        autocompleteFragment?.view?.setBackgroundColor(Color.WHITE)
        // Set the text color (assuming you want black text)
        autocompleteFragment?.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // Handle the selected place
                val location = place.latLng
                if (location != null) {
                    // Add a marker to the selected location on the map
                    googleMap?.addMarker(MarkerOptions().position(location).title(place.name))
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLng(location))
                    name = place.name ?: ""
                    latitudine = location.latitude
                    longitudine = location.longitude
                }
            }

            override fun onError(status: com.google.android.gms.common.api.Status) {
                // Handle errors
                Toast.makeText(requireContext(), "Error: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
            }
        })

        binding.saveButton.setOnClickListener {
            val navController = findNavController()
            val location: Location = Location(name, latitudine, longitudine, "", "","")
            val locationJson = gson.toJson(location)
            editor.putString("currentLocation", locationJson)

            // Apply the changes to SharedPreferences
            editor.apply()
            // Navigate back to the previous destination
            navController.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
