package com.eltonkola.kidztv.ui

import android.Manifest
import android.content.Intent
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eltonkola.kidztv.data.*
import com.eltonkola.kidztv.model.AppElement
import com.eltonkola.kidztv.model.VideoElement
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.io.File

class MainViewModel (private val sharedPreferencesManager : SharedPreferencesManager,
                     private val appManager: AppManager,
                     private val videoManager: VideoManager
) : ViewModel() {

    val permissionState = MutableLiveData<PermissionState>()

    enum class PermissionState {
        CHECKING,
        PERMISSION_OK,
        PERMISSION_KO
    }


    val loading = MutableLiveData<Boolean>()
    var videos = MutableLiveData<List<VideoElement>>()
    private val compositeDisposable = CompositeDisposable()

    val loading_apps = MutableLiveData<Boolean>()
    var apps = MutableLiveData<List<AppElement>>()

    init {
        val fileObserver = MyFileObserver(videoManager.sdCardPath)
        loading_apps.postValue(true)

        compositeDisposable.add(appManager.getWhitelistedApps().subscribe({
            loading_apps.postValue(false)
            apps.postValue(it)
        },{
            loading_apps.postValue(false)
        }))

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
    }

    fun reloadVideos() {
        loading.postValue(true)
//        videos.postValue(videoManager.getVideos())
//        loading.postValue(false)

        compositeDisposable.add(videoManager.loadVideos().subscribe({
            videos.postValue(it)
            loading.postValue(false)
        },{
            it.printStackTrace()
            loading.postValue(false)
        }))
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

    fun isPinCorrect(otp: String): Boolean {
        return sharedPreferencesManager.getPin().toString() == otp
    }

    fun isPinSet(): Boolean {
        return sharedPreferencesManager.hasPin
    }

    fun openApp(app: AppElement) : Intent {
        return appManager.getIntentForPackage(app.packageName)
    }

}