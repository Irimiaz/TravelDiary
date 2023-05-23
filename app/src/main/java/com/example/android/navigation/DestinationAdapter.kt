package com.example.android.navigation

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.android.navigation.database.Destination
import com.example.android.navigation.R
import com.example.android.navigation.database.User
import com.example.android.navigation.database.UserDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DestinationAdapter(private val destinations: List<Destination>,  private val user: User) :
    RecyclerView.Adapter<DestinationAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeName: TextView = itemView.findViewById(R.id.placeName)
        val date: TextView = itemView.findViewById(R.id.date)
        val placeLocation: TextView = itemView.findViewById(R.id.placeLocation)
        val notes: TextView? = itemView.findViewById(R.id.Notes)
        val mood: TextView? = itemView.findViewById(R.id.spinner)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val editButton: ImageButton = itemView.findViewById(R.id.Edit)
        val deleteButton: ImageButton = itemView.findViewById(R.id.Delete)
        var user: User? = null

        init {
            editButton.setOnClickListener {
                val destination = Destination(
                    place = placeName.text.toString(),
                    date = date.text.toString(),
                    travelType = placeLocation?.text.toString(),
                    notes = notes?.text.toString(),
                    mood = mood?.text.toString().toIntOrNull() ?: 0,
                    imageResourceId = getActualImageResourceId(imageView)
                )
                Log.d("notes", destination.id.toString()) // Add this line
                val user = user
                if(user != null) {
                    val action = GameFragmentDirections.actionGameFragmentToEditFragment(destination, user)
                    it.findNavController().navigate(action)
                }

            }
            deleteButton.setOnClickListener {
                val destinationToDelete = Destination(
                    place = placeName.text.toString(),
                    date = date.text.toString(),
                    travelType = placeLocation?.text.toString(),
                    notes = notes?.text.toString(),
                    mood = mood?.text.toString().toIntOrNull() ?: 0,
                    imageResourceId = getActualImageResourceId(imageView)
                )

                user?.let { user ->
                    val updatedDestinations = user.destinations?.toMutableList()
                    val destinationIndex = updatedDestinations?.indexOfFirst { dest ->
                        dest.place == destinationToDelete.place &&
                                dest.date == destinationToDelete.date &&
                                dest.travelType == destinationToDelete.travelType
                    }

                    if (destinationIndex != null && destinationIndex >= 0) {
                        updatedDestinations.removeAt(destinationIndex)
                    }

                    user.destinations = updatedDestinations?.toList()

                    val userDao = UserDatabase.getInstance(itemView.context)?.userDao
                    CoroutineScope(Dispatchers.IO).launch {
                        userDao?.updateUser(user)

                        withContext(Dispatchers.Main) {
                            val navController = itemView.findNavController()
                            val action = GameFragmentDirections.actionGameFragmentSelf(user)
                            navController.navigate(action)
                        }
                    }

                    return@setOnClickListener
                }
            }









            imageView.setOnClickListener {
                // Handle click on the image view if needed
            }
           }
        fun bind(destination: Destination) {
            placeName.text = destination.place
            date.text = destination.date
            mood?.text = destination.mood?.toString()
            notes?.text = destination.notes
            placeLocation.text = destination.travelType
            imageView.setImageResource(destination.imageResourceId) // Load image resource
            imageView.setTag(R.id.imageResourceTag, destination.imageResourceId) // Set the tag for later retrieval
        }

        private fun getActualImageResourceId(imageView: ImageView): Int {
            // Retrieve the image resource ID from the ImageView's tag
            return imageView.getTag(R.id.imageResourceTag) as? Int ?: 0
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_view_item,
            parent,
            false
        )
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val destination = destinations[position]
        holder.user = user // Replace this with the logic to get the user
        holder.bind(destination)
    }

    override fun getItemCount(): Int {
        return destinations.size
    }
}
