package com.eltonkola.kidztv.ui

import android.Manifest
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eltonkola.kidztv.data.AppFolder
import com.eltonkola.kidztv.data.MyFileObserver
import com.eltonkola.kidztv.model.VideoElement
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.io.File

class MainViewModel : ViewModel() {

    val permissionState = MutableLiveData<PermissionState>()

    enum class PermissionState {
        CHECKING,
        PERMISSION_OK,
        PERMISSION_KO
    }

    val sdCardPath: String get() = AppFolder().sdCardPath

    val loading = MutableLiveData<Boolean>()
    var videos = MutableLiveData<List<VideoElement>>()
    private val compositeDisposable = CompositeDisposable()

    val fileObserver: MyFileObserver

    init {
        fileObserver = MyFileObserver(sdCardPath)
        loading.postValue(true)

    }

    fun loadVideos() {

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

    private fun reloadVideos() {
        videos.postValue(File(sdCardPath).walkTopDown().filter { !it.isDirectory }.toList().map { VideoElement(it) })
    }


    public override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun checkPermissions(mainActivity: MainActivity) {
        permissionState.postValue(PermissionState.CHECKING)
        val rxPermissions = RxPermissions(mainActivity)
        compositeDisposable.add(rxPermissions
            .request(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .subscribe { granted ->
                if (granted) { // Always true pre-M
                    permissionState.postValue(PermissionState.PERMISSION_OK)
                } else {
                    permissionState.postValue(PermissionState.PERMISSION_KO)
                }
            })

    }

}