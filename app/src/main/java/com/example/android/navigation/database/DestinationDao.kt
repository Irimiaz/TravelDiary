package com.example.android.navigation.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DestinationDao {
    @Insert
    suspend fun insert(destination: Destination)

    @Query("SELECT * FROM destination")
    suspend fun getAllDestinations(): List<Destination>
}