package com.eltonkola.kidztv.ui.openVideoPlugin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eltonkola.kidztv.model.VideoElement
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

class SampleVideoViewModel : ViewModel() {

    val loading = MutableLiveData<Boolean>()
    var videos = MutableLiveData<List<VideoElement>>()
    private val compositeDisposable = CompositeDisposable()

    init {
        reloadVideos()
    }

    private fun reloadVideos() {
        compositeDisposable.add(getVideos().subscribe(
            { videos ->
                Timber.i("@@@ videos: ${videos.size}")

            },
            { t ->
                Timber.e(t)
            }
        ))

    }

    private val DATA_URL = "fakeurl"

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