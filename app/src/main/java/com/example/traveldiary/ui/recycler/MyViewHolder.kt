package com.example.traveldiary.ui.recycler

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.traveldiary.R  // Import your app's resources
import com.example.traveldiary.database.Destination

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val nameView: TextView = itemView.findViewById(R.id.destinationName)
    private val descriptionView: TextView = itemView.findViewById(R.id.destinationDescription)
    private val descriptionDate: TextView = itemView.findViewById(R.id.destinationDate)
    private val photo : ImageView = itemView.findViewById(R.id.destinationPhoto)
    fun bind(destination: Destination) {
        // Bind the destination data to the views
        nameView.text = destination.name
        descriptionView.text = destination.description
        descriptionDate.text = destination.date
        Glide.with(itemView)
            .load(destination.photo) // Replace with the actual image URL or resource ID
            .apply(RequestOptions().fitCenter()) // Use fitCenter to maintain aspect ratio
            .transition(DrawableTransitionOptions.withCrossFade()) // Add a crossfade animation
            .into(photo)

    }

}
