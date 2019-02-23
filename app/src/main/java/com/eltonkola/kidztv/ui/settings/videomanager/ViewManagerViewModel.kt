package com.eltonkola.kidztv.ui.settings.videomanager

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eltonkola.kidztv.data.MyFileObserver
import com.eltonkola.kidztv.data.VideoManager
import com.eltonkola.kidztv.model.VideoElement
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class ViewManagerViewModel(private val context: Context,
                           private val videoManager: VideoManager) : ViewModel() {

    val loading = MutableLiveData<Boolean>()
    var videos = MutableLiveData<List<VideoElement>>()
    private val compositeDisposable = CompositeDisposable()

    init {
        val fileObserver = MyFileObserver(videoManager.sdCardPath)
        compositeDisposable.add(fileObserver.observable
            .subscribe(
                { path ->
                    Timber.i("@@@ path: $path")
                    reloadVideos()
                },
                { t ->
                    Timber.e(t)
                }
            ))
        reloadVideos()
    }

    fun reloadVideos() {
        loading.postValue(true)
        compositeDisposable.add(videoManager.loadVideos().subscribe({
            videos.postValue(it)
            loading.postValue(false)
        },{
            loading.postValue(false)
        }))
    }

    public override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun deleteFile(element: VideoElement) {
        loading.postValue(true)
        compositeDisposable.add(videoManager.deleteFile(element).subscribe({
            reloadVideos()
        },{
            Toast.makeText(context, "Error deleting video ${element.file.name}", Toast.LENGTH_SHORT).show()
            loading.postValue(false)
        }))

    }


}