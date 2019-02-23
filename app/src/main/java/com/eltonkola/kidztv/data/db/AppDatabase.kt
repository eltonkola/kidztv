package com.eltonkola.kidztv.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.eltonkola.kidztv.model.db.UserApp

@Database(entities = arrayOf(UserApp::class), version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userAppDao(): UserAppDao
}