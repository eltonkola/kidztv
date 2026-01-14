package com.mrkola.kidztv.di

import com.mrkola.kidztv.data.VideoRepository
import com.mrkola.kidztv.ui.ParentalControlsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Provide VideoRepository (Assuming it needs a Context or has a default constructor)
    // If it needs context, use get()
    single { VideoRepository(get()) }

    // Provide ViewModel
    viewModel { ParentalControlsViewModel(get()) }
}