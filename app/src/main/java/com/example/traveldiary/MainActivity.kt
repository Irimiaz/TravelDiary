package com.example.traveldiary

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.traveldiary.database.User
import com.example.traveldiary.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    companion object {
        var userList = ArrayList<User>()
        var currentUser : User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()


        val sharedPref = getSharedPreferences("myPref", MODE_PRIVATE)
        val currentUserJson = sharedPref.getString("currentUser", null)
        val editor = sharedPref.edit()
        val gson = Gson()
        if (!currentUserJson.isNullOrEmpty()) {
            val currentUser: User = gson.fromJson(currentUserJson, User::class.java)
            if (currentUser.settings.language.equals("Romanian") || currentUser.settings.language.equals("Romana")) {
                val locale = Locale("ro")
                val config = Configuration(resources.configuration)
                config.setLocale(locale)
                resources.updateConfiguration(config, resources.displayMetrics)
            } else {
                val config = Configuration(resources.configuration)
                config.setToDefaults()
                resources.updateConfiguration(config, resources.displayMetrics)
            }
        }
        getSupportActionBar()?.setDisplayShowTitleEnabled(false);

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.title = "Travel Diary"
        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        getActionBar()?.setDisplayShowTitleEnabled(false);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top-level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_settings
            ),
            drawerLayout
        )
        val userListJson = sharedPref.getString("userList", null)

        if (userListJson != null) {
            // Deserialize the JSON string into userList
            val gson = Gson()
            val userListType = object : TypeToken<List<User>>() {}.type
            userList = gson.fromJson(userListJson, userListType)
        } else {
            // Initialize an empty userList if no data found
            userList = ArrayList()
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        drawerLayout.closeDrawer(GravityCompat.START)

        if (!currentUserJson.isNullOrEmpty()) {
            val currentUser: User = gson.fromJson(currentUserJson, User::class.java)

            if (currentUser.settings.nightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        updateMenuItems()
    }

    private fun updateMenuItems() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        drawerLayout.closeDrawer(GravityCompat.START)
        menuInflater.inflate(R.menu.main, menu)
        val upButton = menu.findItem(android.R.id.home)
        upButton?.isVisible = false
        getActionBar()?.setDisplayHomeAsUpEnabled(false);
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(false)
        val item = menu.findItem(R.id.action_settings)
        item.setVisible(false)
        //menu item invisible above
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection based on the selected menu item's ID
        drawerLayout.closeDrawer(GravityCompat.START)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val navView: NavigationView = binding.navView
        navView.setupWithNavController(navController)
        if(item.title?.equals(R.string.settings) == true) {
            navController.navigate(R.id.nav_settings)
        }
        val usernameHeader = findViewById<TextView>(R.id.usernameHeader)
        val usermailHeader = findViewById<TextView>(R.id.usermailHeader)

        val sharedPref = getSharedPreferences("myPref", MODE_PRIVATE)
        val currentJson = sharedPref.getString("currentUser", null)
        val userPhotoHeader = findViewById<ImageView>(R.id.userPhotoHeader)
        userPhotoHeader.setOnLongClickListener { view ->
            val popupMenu = PopupMenu(this, view) // Pass the context and the view
            popupMenu.menuInflater.inflate(R.menu.popitem, popupMenu.menu) // Define your menu items in XML (res/menu/popup_menu.xml)

            // Set a click listener for the items in the popup menu
            popupMenu.setOnMenuItemClickListener { item ->
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, 100)
                true
            }

            popupMenu.show()
            true // Return true to consume the event
        }
        if (currentJson != null) {
            val gson = Gson()
            val currentUser = gson.fromJson(currentJson, User::class.java)
            usernameHeader.text = currentUser.fullName
            usermailHeader.text = currentUser.mail

            // Load the user's photo using Glide
            Glide.with(this)
                .load(Uri.parse(currentUser.photo))
                .into(userPhotoHeader)
        } else {
        }


        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_settings -> {
                    navController.navigate(R.id.nav_settings)
                }
                R.id.nav_login -> {
                    val builder = AlertDialog.Builder(this)

                    // Set the dialog title and message
                    builder.setTitle(R.string.sureQuestion)
                        .setMessage(R.string.logoutQuestion)

                    // Add "Yes" button
                    builder.setPositiveButton(R.string.yes) { _, _ ->
                        // User clicked "Yes," navigate to the login screen
                        navController.navigate(R.id.nav_login)
                        val preferences = getSharedPreferences("myPref", 0)
                        preferences.edit().remove("currentDestination").commit()


                        val sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        val gson = Gson()
                        val userListJson = sharedPref.getString("userList", null)
                        val currentUserJson = sharedPref.getString("currentUser", null)
                        val currentUser = gson.fromJson(currentUserJson, User::class.java)
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

                R.id.nav_delete -> {
                    val builder = AlertDialog.Builder(this)

                    // Set the dialog title and message
                    builder.setTitle(R.string.sureQuestion)
                        .setMessage(R.string.deleteAccountQuestion)

                    // Add "Yes" button
                    builder.setPositiveButton(R.string.yes) { _, _ ->
                        // User clicked "Yes," perform the delete action here
                        val sharedPref = getSharedPreferences("myPref", MODE_PRIVATE)
                        val currentJson = sharedPref.getString("currentUser", null)
                        val userListJson = sharedPref.getString("userList", null)

                        // Check if there is a valid user object in SharedPreferences
                        val gson = Gson()
                        val currentUser = gson.fromJson(currentJson, User::class.java)

                        if (userListJson != null) {
                            val userListType = object : TypeToken<List<User>>() {}.type
                            val userList: MutableList<User> = gson.fromJson(userListJson, userListType)

                            // Find the current user in the userList
                            val currentUserInList = getUserByUsernameAndPassword(currentUser.username, currentUser.password, userList)

                            if (currentUserInList != null) {
                                // Remove the user from the userList
                                userList.remove(currentUserInList)

                                // Update the userList in SharedPreferences
                                val editor = sharedPref.edit()
                                val updatedUserListJson = gson.toJson(userList)
                                editor.putString("userList", updatedUserListJson)
                                editor.apply()

                                // Display a Toast message
                                Toast.makeText(this, R.string.accountDeleted, Toast.LENGTH_SHORT).show()

                                // Navigate to the login screen or perform any other desired action
                                navController.navigate(R.id.nav_login)

                                // Remove the "currentUser" key from SharedPreferences
                                editor.remove("currentUser")
                                editor.remove("currentDestination")
                                editor.apply()
                            }
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






                R.id.nav_delete_all -> {
                    // Create a dialog to prompt for the password
                    val builder = AlertDialog.Builder(this) // Use the appropriate context

                    // Inflate the password input layout
                    val inflater = layoutInflater
                    val dialogView = inflater.inflate(R.layout.delete_all, null)
                    builder.setView(dialogView)

                    val passwordEditText = dialogView.findViewById<EditText>(R.id.passwordEditText)
                    val submitButton = dialogView.findViewById<Button>(R.id.submitButton)

                    // Create the dialog
                    val dialog = builder.create()

                    // Set up the "Submit" button click listener
                    submitButton.setOnClickListener {
                        val enteredPassword = passwordEditText.text.toString()

                        // Check if the entered password is correct (replace "your_password" with the actual password)
                        val correctPassword = "test"

                        if (enteredPassword == correctPassword) {
                            // Password is correct, show a Toast message
                            Toast.makeText(this, R.string.dataDeleted, Toast.LENGTH_SHORT).show()

                            // Dismiss the dialog
                            dialog.dismiss()

                            // Perform the action you want when the password is correct
                            // For example, show a specific view or perform a delete operation here
                            navController.navigate(R.id.nav_login)
                            val preferences = getSharedPreferences("myPref", 0)
                            preferences.edit().remove("currentUser").commit()
                            preferences.edit().remove("userList").commit()
                            preferences.edit().remove("currentDestination").commit()

                        } else {
                            // Password is incorrect, show a Toast message
                            Toast.makeText(this, R.string.incorrectPassword, Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Show the dialog
                    dialog.show()
                }

            }
            navigate(item)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getUserByUsernameAndPassword(username: String, password: String, userList: List<User>): User? {
        for (user in userList) {
            if (user.username == username && user.password == password) {
                return user
            }
        }
        Toast.makeText(this, R.string.noUserFound, Toast.LENGTH_SHORT).show()
        return null
    }

    fun navigate(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val navView: NavigationView = binding.navView
        navView.setupWithNavController(navController)
        when (item.itemId) {
            R.id.nav_new_destination -> {
                navController.navigate(R.id.nav_new_destination)
                return true
            }
            R.id.nav_home -> {
                navController.navigate(R.id.nav_home)
                val test = userList
                val test2 = currentUser
                return true
            }
            R.id.nav_gallery -> {
                navController.navigate(R.id.nav_gallery)
                return true
            }
            R.id.nav_slideshow -> {
                navController.navigate(R.id.nav_slideshow)
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data

            if (selectedImageUri != null) {
                // Load the selected image into the userPhotoHeader ImageView
                val sharedPref = getSharedPreferences("myPref", MODE_PRIVATE)
                val currentJson = sharedPref.getString("currentUser", null)

                // Check if there is a valid user object in SharedPreferences
                if (currentJson != null) {
                    val gson = Gson()
                    val currentUser = gson.fromJson(currentJson, User::class.java)

                    // Update the user's photo property
                    currentUser.photo = selectedImageUri.toString()

                    // Save the updated user object back to SharedPreferences
                    val editor = sharedPref.edit()
                    val updatedJson = gson.toJson(currentUser)
                    editor.putString("currentUser", updatedJson)
                    val userListJson = sharedPref.getString("userList", null)
                    val userListType = object : TypeToken<List<User>>() {}.type
                    var userList = gson.fromJson<List<User>>(userListJson, userListType)
                    if (userList == null) {
                        userList = ArrayList()
                    }

                    // Find the current user in the user array and update it
                    userList = userList.map { if (it.username == currentUser.username) currentUser else it }
                    val updatedUserListJson = gson.toJson(userList)
                    editor.putString("userList", updatedUserListJson)
                    editor.apply()
                    val userPhotoHeader = findViewById<ImageView>(R.id.userPhotoHeader)
                    // Load the selected image into the userPhotoHeader ImageView using Glide
                    Glide.with(this)
                        .load(selectedImageUri)
                        .into(userPhotoHeader)
                }
            }
        }
    }



    override fun onSupportNavigateUp(): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        getActionBar()?.setDisplayHomeAsUpEnabled(false);
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}