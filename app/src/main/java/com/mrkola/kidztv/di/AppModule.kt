package com.mrkola.kidztv.di

import androidx.room.Room
import com.mrkola.kidztv.data.KidzTvDatabase
import com.mrkola.kidztv.data.VideoDownloader
import com.mrkola.kidztv.data.VideoRepository
import com.mrkola.kidztv.ui.MainViewModel
import com.mrkola.kidztv.ui.ParentalControlsViewModel
import com.mrkola.kidztv.ui.PlayerViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.downloader.Downloader

val appModule = module {

    single {
        Room.databaseBuilder(
            androidApplication(),
            KidzTvDatabase::class.java,
            "kidztv.db"
        ).fallbackToDestructiveMigration().build()
    }

    single { get<KidzTvDatabase>().videoDao() }

    single<Downloader> {
        NewPipe.init(VideoDownloader())
        NewPipe.getDownloader()
    }
    single { VideoRepository(get(), get(), get()) }

    viewModel { (videoId: String) ->
        PlayerViewModel(
            application = androidApplication(),
            videoRepository = get(),
            initialVideoId = videoId
        )
    }

    viewModel { ParentalControlsViewModel(get(), androidApplication()) }
    viewModel { MainViewModel(get()) }


}