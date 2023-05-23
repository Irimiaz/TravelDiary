

package com.example.android.navigation


import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.android.navigation.database.UserDao
import com.example.android.navigation.database.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class TitleFragment : Fragment() {

    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var userDao: UserDao

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_title, container, false)
        loginButton = view.findViewById(R.id.loginButton)
        registerButton = view.findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            view.findNavController().navigate(TitleFragmentDirections.actionTitleFragmentToRegisterFragment4())
        }
        val userDatabase = UserDatabase.getInstance(requireContext())
        userDao = userDatabase.userDao
        loginButton.setOnClickListener {
            // Get the input values from the EditText fields
            val username = view.findViewById<EditText>(R.id.username).text.toString()
            val password = view.findViewById<EditText>(R.id.password).text.toString()

            // Check if a user with the same username and password already exists
            GlobalScope.launch(Dispatchers.Main) {
                val existingUser = userDao.getUserByUsernameAndPassword(username, password)
                if (existingUser == null) {
                    // User with the same username and password already exists
                    Toast.makeText(requireContext(), "User does not exist!", Toast.LENGTH_SHORT).show()
                } else {
                    val action = TitleFragmentDirections.actionTitleFragmentToGameFragment(existingUser)
                    view.findNavController().navigate(action)
                }
            }
        }

        setHasOptionsMenu(true)

        return view
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

}






