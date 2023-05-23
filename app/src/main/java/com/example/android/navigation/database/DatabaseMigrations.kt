package com.example.android.navigation.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
object DatabaseMigrations {
    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Perform a destructive migration (drops the existing table and recreates it)
            database.execSQL("DROP TABLE IF EXISTS user")
            database.execSQL("CREATE TABLE user (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, fullName TEXT NOT NULL, password TEXT NOT NULL, email TEXT NOT NULL, username TEXT NOT NULL)")
        }
    }
}

