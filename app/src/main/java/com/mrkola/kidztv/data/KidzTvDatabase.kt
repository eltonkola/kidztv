package com.mrkola.kidztv.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [VideoEntity::class], version = 1, exportSchema = false)
abstract class KidzTvDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao
}
