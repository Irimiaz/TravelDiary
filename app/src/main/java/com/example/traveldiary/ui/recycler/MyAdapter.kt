package com.example.traveldiary.ui.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.traveldiary.R  // Import your app's resources
import com.example.traveldiary.database.Destination  // Import your Destination class

class MyAdapter(private val context: Context, private val destinations: List<Destination>, private val onItemClick: (Destination) -> Unit) :

    RecyclerView.Adapter<MyViewHolder>() {
    private val sharedPreferences = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_view, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val destination = destinations[position]
        holder.bind(destination)
        holder.itemView.setOnClickListener {
            onItemClick(destination) // Pass the destination name to the callback
        }
    }

    override fun getItemCount(): Int {
        return destinations.size
    }
}
