package com.example.traveldiary.ui.contact_us

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.traveldiary.R
import com.example.traveldiary.databinding.FragmentContactUsBinding

class ContactUsFragment : Fragment() {

private var _binding: FragmentContactUsBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val slideshowViewModel =
            ViewModelProvider(this).get(ContactUsViewModel::class.java)

    _binding = FragmentContactUsBinding.inflate(inflater, container, false)
    val root: View = binding.root
      binding.linkedinButton.setOnClickListener {
          openGoogleSite("https://www.linkedin.com/in/marian-irimia-81637b253/") // Replace with your LinkedIn URL

      }
      binding.linkedInText.setOnClickListener {
          openGoogleSite("https://www.linkedin.com/in/marian-irimia-81637b253/") // Replace with your LinkedIn URL

      }
      binding.githubButton.setOnClickListener {
          openGoogleSite("https://github.com/Irimiaz") // Replace with your LinkedIn URL

      }
      binding.githubText.setOnClickListener {
          openGoogleSite("https://github.com/Irimiaz") // Replace with your LinkedIn URL

      }

      binding.sendButton.setOnClickListener {
            val subject : String = binding.subjectText.text.toString()
            val content : String = binding.description.text.toString()
          if(subject.isEmpty()) {
              Toast.makeText(requireContext(), R.string.pleaseWriteTheSubject, Toast.LENGTH_SHORT).show()
          } else {
              sendEmail(subject,content)
          }
      }
      return root
  }

    private fun openGoogleSite(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    fun sendEmail (subject : String, content : String) {
            val intent: Intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("marianirimia027@gmail.com"))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, content)
        intent.setType("message/rfc822")
        startActivity(Intent.createChooser(intent, "Send Email"))
        binding.description.text.clear()
        binding.subjectText.text.clear()
    }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}