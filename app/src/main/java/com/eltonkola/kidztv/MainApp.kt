package com.eltonkola.kidztv


import android.app.Application
import android.util.Log
import androidx.room.Room
import com.eltonkola.kidztv.data.db.AppDatabase
import timber.log.Timber

import timber.log.Timber.DebugTree

class MainApp : Application() {


    override fun onCreate() {
        super.onCreate()

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "app-data").build()


        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    Log.e(tag, message, t)
                }
            })
        }
    }

}