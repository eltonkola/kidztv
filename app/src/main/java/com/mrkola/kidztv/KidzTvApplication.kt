package com.mrkola.kidztv

import android.app.Application
import com.mrkola.kidztv.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KidzTvApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@KidzTvApplication)
            modules(appModule)
        }
    }
}