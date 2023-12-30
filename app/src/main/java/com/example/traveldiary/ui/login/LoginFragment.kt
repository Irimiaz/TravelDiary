package com.example.traveldiary.ui.login

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.traveldiary.MainActivity
import com.example.traveldiary.R
import com.example.traveldiary.database.User
import com.example.traveldiary.databinding.FragmentLoginBinding  // Import the correct binding class
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null  // Use FragmentLoginBinding
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(LoginViewModel::class.java)
        _binding = FragmentLoginBinding.inflate(inflater, container, false)  // Use FragmentLoginBinding
        val root: View = binding.root

        val LoginButton : Button = binding.loginButton
        val RegisterButton: Button = binding.registerButton
        RegisterButton.setOnClickListener {
            // Set the action bar title
            (requireActivity() as AppCompatActivity).supportActionBar?.title = "Register"

            // Use NavController to navigate to the nav_register fragment
            findNavController().navigate(R.id.nav_register)
        }

        val mainActivity = activity as? MainActivity
        mainActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)


        LoginButton.setOnClickListener {
            val username: String = binding.username.text.toString()
            val password: String = binding.password.text.toString()
            val sharedPref = requireActivity().getSharedPreferences("myPref", MODE_PRIVATE)
            val editor = sharedPref.edit()

            val userListJson = sharedPref.getString("userList", null)
            var found: Boolean = false

            // Check if userListJson is not null and not empty
            if (!userListJson.isNullOrEmpty()) {
                // Deserialize the userList from SharedPreferences
                val gson = Gson()
                val userListType = object : TypeToken<ArrayList<User>>() {}.type
                val userList = gson.fromJson<ArrayList<User>>(userListJson, userListType)

                // Find the user with matching credentials
                val currentUser = userList.find { user ->
                    user.password == password && user.username == username
                }

                if (currentUser != null) {


                    // Convert the current user to JSON and save it as "currentUser" in SharedPreferences
                    val currentJson = gson.toJson(currentUser)
                    editor.putString("currentUser", currentJson)
                    editor.apply()
                    found = true

                    findNavController().navigate(R.id.nav_home)
                }
            }

            if (!found) {
                Toast.makeText(requireContext(), R.string.wrongCredentials , Toast.LENGTH_SHORT).show()
            }
        }






        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
