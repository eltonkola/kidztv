package com.mrkola.kidztv.di

import com.mrkola.kidztv.data.VideoDownloader
import com.mrkola.kidztv.data.VideoRepository
import com.mrkola.kidztv.ui.ParentalControlsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.downloader.Downloader

val appModule = module {

//    single { VideoDownloader() }

    single<Downloader> {
        NewPipe.init(VideoDownloader())
        NewPipe.getDownloader()
    }
    single { VideoRepository(get(), get()) }


    viewModel { ParentalControlsViewModel(get()) }


}