package com.example.traveldiary.ui.new_destination

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewDestinationViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is register Fragment"
    }
    val text: LiveData<String> = _text
}
