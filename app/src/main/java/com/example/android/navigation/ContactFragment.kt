package com.example.android.navigation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.android.navigation.R

class ContactFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact, container, false)

        // Find the LinkedIn and GitHub buttons by their IDs
        val linkedinButton = view.findViewById<ImageButton>(R.id.linkedin)
        val githubButton = view.findViewById<ImageButton>(R.id.github)

        // Find the LinkedIn and GitHub text views by their IDs
        val linkedinText = view.findViewById<TextView>(R.id.textView)
        val githubText = view.findViewById<TextView>(R.id.textView2)

        // Set click listeners for the buttons
        linkedinButton.setOnClickListener {
            openGoogleSite("https://www.linkedin.com/in/marian-irimia-81637b253/") // Replace with your LinkedIn URL
        }

        githubButton.setOnClickListener {
            openGoogleSite("https://github.com/Irimiaz") // Replace with your GitHub URL
        }

        // Set click listeners for the text views
        linkedinText.setOnClickListener {
            openGoogleSite("https://www.linkedin.com/in/marian-irimia-81637b253/") // Replace with your LinkedIn URL
        }

        githubText.setOnClickListener {
            openGoogleSite("https://github.com/Irimiaz") // Replace with your GitHub URL
        }

        return view
    }

    private fun openGoogleSite(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
