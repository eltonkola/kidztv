package com.eltonkola.kidztv.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.eltonkola.kidztv.data.AppFolder
import com.eltonkola.kidztv.data.MyFileObserver
import com.eltonkola.kidztv.model.VideoElement
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.io.File


class ViewManagerViewModel(context: Application) : AndroidViewModel(context) {

    val sdCardPath: String get() = AppFolder().sdCardPath

    val loading = MutableLiveData<Boolean>()
    var videos = MutableLiveData<List<VideoElement>>()
    private val compositeDisposable = CompositeDisposable()

    val fileObserver: MyFileObserver

    init {
        fileObserver = MyFileObserver(sdCardPath)
        loadVideos()
    }

    fun loadVideos() {
        loading.postValue(true)
        compositeDisposable.add(fileObserver.observable
            .subscribe(
                { path ->
                    Timber.i("@@@ path: $path")
                    reloadVideos()
                },
                { t ->
                    Timber.e(t)
                    loading.postValue(false)
                }
            ))
        reloadVideos()
    }

    private fun reloadVideos() {
        videos.postValue(File(sdCardPath).walkTopDown().filter { !it.isDirectory }.toList().map { VideoElement(it) })
        loading.postValue(false)
    }


    public override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun deleteFile(element: VideoElement) {
        loading.postValue(true)
        val file = File(element.file.getPath())
        file.delete()
        if (file.exists()) {
            file.getCanonicalFile().delete()
            if (file.exists()) {
                getApplication<Application>().deleteFile(file.getName())
            }
        }
        reloadVideos()
    }


}