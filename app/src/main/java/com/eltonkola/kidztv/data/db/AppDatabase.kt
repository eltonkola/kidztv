package com.eltonkola.kidztv.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.eltonkola.kidztv.model.db.UserApp

@Database(entities = arrayOf(UserApp::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userAppDao(): UserAppDao
}