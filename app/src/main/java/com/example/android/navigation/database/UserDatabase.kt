package com.example.android.navigation.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [User::class, Destination::class], version = 2, exportSchema = false)
@TypeConverters(DestinationConverter::class)
abstract class UserDatabase : RoomDatabase() {

    abstract val userDao: UserDao
    abstract val destinationDao: DestinationDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        UserDatabase::class.java,
                        "user_database"
                    )
                        .fallbackToDestructiveMigration() // Add fallback to destructive migration
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
