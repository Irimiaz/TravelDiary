package com.example.android.navigation.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DestinationConverter {
    private val gson = Gson()

    @TypeConverter
    @JvmStatic
    fun fromString(value: String?): List<Destination> {
        val listType = object : TypeToken<List<Destination>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    @JvmStatic
    fun toString(destinations: List<Destination>?): String {
        return gson.toJson(destinations)
    }
}
