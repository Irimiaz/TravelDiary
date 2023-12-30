package com.example.traveldiary.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.traveldiary.MainActivity

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        if (MainActivity.currentUser?.destinationList == null) {
            value = "No destination added"
        }
        else {
            value = ""
        }
    }
    val text: LiveData<String> = _text
}