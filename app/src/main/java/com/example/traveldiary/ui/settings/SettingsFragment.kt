package com.example.traveldiary.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.traveldiary.MainActivity
import com.example.traveldiary.R
import com.example.traveldiary.database.Destination
import com.example.traveldiary.database.User
import com.example.traveldiary.databinding.FragmentSettingsBinding
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import java.util.*


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val homeViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        val sharedPref = requireActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val currentUserJson = sharedPref.getString("currentUser", null)
        val editor = sharedPref.edit()
        val gson = Gson()
        if(currentUserJson != null) {
            val currentUser: User = gson.fromJson(currentUserJson, User::class.java)
            binding.darkModeSwitch.setChecked(currentUser.settings.nightMode)
            binding.satelliteSwitch.setChecked(currentUser.settings.satellite)
        }

        val spinner = binding.language
        var type = ""
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.languages,
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

        binding.saveButton.setOnClickListener {
            // Get the values of the switches
            val isDarkModeEnabled = binding.darkModeSwitch.isChecked
            val isSatelliteEnabled = binding.satelliteSwitch.isChecked

            // Create a toast message with the switch values
            if(currentUserJson == null) {
                Toast.makeText(requireContext(), R.string.pleaseLogInFirst , Toast.LENGTH_SHORT).show()
            } else {
                val currentUser: User = gson.fromJson(currentUserJson, User::class.java)
                currentUser.settings.satellite=isSatelliteEnabled
                currentUser.settings.nightMode=isDarkModeEnabled
                if(!type.equals("")) {
                    currentUser.settings.language = type
                }
                val updatedCurrentUserJson = gson.toJson(currentUser)
                editor.putString("currentUser", updatedCurrentUserJson)
                // Apply the changes to SharedPreferences
                editor.apply()
                if(type.equals("Romanian") || type.equals("Romana")) {
                    val locale = Locale("ro") // Change to French
                    val config = Configuration(resources.configuration)
                    config.setLocale(locale)
                    resources.updateConfiguration(config, resources.displayMetrics)
                    updateMenuItems()
                } else if(type.equals("English") || type.equals("Engleza")){
                    val config = Configuration(resources.configuration)
                    config.setToDefaults()
                    resources.updateConfiguration(config, resources.displayMetrics)
                    updateMenuItems()
                }
                if(isDarkModeEnabled) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                if(type.equals("Romanian") || type.equals("Romana")) {
                    val locale = Locale("ro") // Change to French
                    val config = Configuration(resources.configuration)
                    config.setLocale(locale)
                    resources.updateConfiguration(config, resources.displayMetrics)
                    updateMenuItems()
                } else if(type.equals("English") || type.equals("Engleza")){
                    val config = Configuration(resources.configuration)
                    config.setToDefaults()
                    resources.updateConfiguration(config, resources.displayMetrics)
                    updateMenuItems()
                }
                Toast.makeText(requireContext(),R.string.settingsSaved, Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.nav_settings)

            }
        }
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.nav_home)
        }
        return root
    }

    private fun updateMenuItems() {
        val mainActivity = requireActivity() as MainActivity
        val navigationView = mainActivity.findViewById<NavigationView>(R.id.nav_view)
        val menu = navigationView.menu

        // Find and update each menu item
        val homeMenuItem = menu.findItem(R.id.nav_home)
        homeMenuItem.title = getString(R.string.home)

        val aboutUsMenuItem = menu.findItem(R.id.nav_gallery)
        aboutUsMenuItem.title = getString(R.string.aboutUs)

        val contactUsMenuItem = menu.findItem(R.id.nav_slideshow)
        contactUsMenuItem.title = getString(R.string.contactUs)

        val settingsMenuItem = menu.findItem(R.id.nav_settings)
        settingsMenuItem.title = getString(R.string.settings)

        val logoutMenuItem = menu.findItem(R.id.nav_login)
        logoutMenuItem.title = getString(R.string.logout)

        val deleteAccountMenuItem = menu.findItem(R.id.nav_delete)
        deleteAccountMenuItem.title = getString(R.string.deleteAccount)

        val deleteDataMenuItem = menu.findItem(R.id.nav_delete_all)
        deleteDataMenuItem.title = getString(R.string.deleteAllData)
        // Repeat this for other menu items...
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}