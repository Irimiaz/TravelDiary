package com.example.traveldiary.ui.about_us

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.traveldiary.databinding.FragmentAboutUsBinding

class AboutUsFragment : Fragment() {

private var _binding: FragmentAboutUsBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val galleryViewModel =
            ViewModelProvider(this).get(AboutUsViewModel::class.java)

    _binding = FragmentAboutUsBinding.inflate(inflater, container, false)
    val root: View = binding.root


    return root
  }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}