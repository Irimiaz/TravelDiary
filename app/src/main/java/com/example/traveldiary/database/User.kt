package com.example.traveldiary.database

import android.net.Uri
import com.example.traveldiary.R

class User(username: String, password: String, mail: String, fullName: String, destinationList: ArrayList<Destination>?, photo : String, settings : Settings) {
    val username: String = username
    val password: String = password
    val mail: String = mail
    val fullName: String = fullName
    var photo : String = photo
    var destinationList: ArrayList<Destination> = destinationList?.toCollection(mutableListOf()) as ArrayList<Destination>
    var settings : Settings = settings
}


