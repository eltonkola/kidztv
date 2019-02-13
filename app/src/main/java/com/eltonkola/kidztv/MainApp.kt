package com.eltonkola.kidztv


import android.app.Application
import android.util.Log
import timber.log.Timber

import timber.log.Timber.DebugTree

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()

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