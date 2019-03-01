package com.eltonkola.kidztv.ui.openVideoPlugin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eltonkola.kidztv.data.VideoManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class SampleVideoViewModel(private val videoManager: VideoManager) : ViewModel() {

    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData<Boolean>()
    var videos = MutableLiveData<List<OpenVideoElement>>()
    private val compositeDisposable = CompositeDisposable()

    init {
        reloadVideos()
    }

    fun reloadVideos() {
        loading.postValue(true)
        compositeDisposable.add(getVideos().subscribe(
            { data ->
                Timber.i("@@@ videos: ${data.size}")

                val normalizedData = data.map {
                    it.alreadyDownloaded = videoManager.alreadyDownloaded(it.getFileName())
                    it
                }

                videos.postValue(normalizedData)
                loading.postValue(false)
            },
            { t ->
                Timber.e(t)
                loading.postValue(false)
                error.postValue(true)
            }
        ))

    }

    private val DATA_URL = "https://raw.githubusercontent.com/eltonkola/kidztv/master/open_videos/videos.json"

    private fun getVideos(): Single<List<OpenVideoElement>> {
        return Single.create<List<OpenVideoElement>> { emitter ->
            try {
                val catalogConn = URL(DATA_URL)
                val reader = BufferedReader(InputStreamReader(catalogConn.openStream()))
                val turnsType = object : TypeToken<List<OpenVideoElement>>() {}.type
                val data = Gson().fromJson<List<OpenVideoElement>>(reader, turnsType)
                emitter.onSuccess(data)
            } catch (error: Exception) {
                emitter.onError(error)
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


    public override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }


}