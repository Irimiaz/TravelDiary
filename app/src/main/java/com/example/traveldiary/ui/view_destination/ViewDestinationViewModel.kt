package com.example.traveldiary.ui.view_destination

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewDestinationViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is about us Fragment"
    }
    val text: LiveData<String> = _text
}