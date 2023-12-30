package com.example.traveldiary.ui.register

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.traveldiary.R
import com.example.traveldiary.database.Destination
import com.example.traveldiary.database.Settings
import com.example.traveldiary.database.User
import com.example.traveldiary.databinding.FragmentRegisterBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val homeViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)


        requireActivity().actionBar?.title = "Register"

        val registerButton: Button = binding.registerButton
        registerButton.setOnClickListener {
            val username: String = binding.username.text.toString()
            val password: String = binding.password.text.toString()
            val mail: String = binding.email.text.toString()
            val fullName: String = binding.name.text.toString()
            val destinationList = ArrayList<Destination>()

            // Retrieve the current userList from SharedPreferences
            val sharedPref = requireActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE)
            val userListJson = sharedPref.getString("userList", null)
            val gson = Gson()

            if (userListJson != null) {
                // Deserialize the userList from SharedPreferences
                val userListType = object : TypeToken<ArrayList<User>>() {}.type
                val userList = gson.fromJson<ArrayList<User>>(userListJson, userListType)
                val packageName : String = "com.example.traveldiary"
                val photo = Uri.parse("android.resource://$packageName/${R.mipmap.ic_launcher_round}").toString()
                val settings = Settings("English", false, false)
                val newUser = User(username, password, mail, fullName, destinationList, photo, settings)

                // Add the new user to the userList
                userList.add(newUser)

                // Serialize the updated userList to JSON
                val updatedUserListJson = gson.toJson(userList)

                // Save the updated userList back to SharedPreferences
                val editor = sharedPref.edit()
                editor.putString("userList", updatedUserListJson)
                editor.apply()

                // Navigate to the login screen or wherever needed
                findNavController().navigate(R.id.nav_login)
            } else {
                // Handle the case where userList doesn't exist in SharedPreferences
                // You might want to create a new userList and save it
                val newUserList = ArrayList<User>()
                val packageName : String = "com.example.traveldiary"
                val photo = Uri.parse("android.resource://$packageName/${R.mipmap.ic_launcher_round}").toString()
                val settings = Settings("English", false, false)
                newUserList.add(User(username, password, mail, fullName, destinationList, photo, settings))
                val updatedUserListJson = gson.toJson(newUserList)
                val editor = sharedPref.edit()
                editor.putString("userList", updatedUserListJson)
                editor.apply()
                // Navigate to the login screen or wherever needed
                findNavController().navigate(R.id.nav_login)
            }
        }


        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}