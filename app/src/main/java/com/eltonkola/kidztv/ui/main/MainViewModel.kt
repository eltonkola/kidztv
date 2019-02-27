package com.eltonkola.kidztv.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eltonkola.kidztv.data.AppManager
import com.eltonkola.kidztv.data.MyFileObserver
import com.eltonkola.kidztv.data.SharedPreferencesManager
import com.eltonkola.kidztv.data.VideoManager
import com.eltonkola.kidztv.model.VideoElement
import com.eltonkola.kidztv.ui.settings.SettingsActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class MainViewModel(
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val appManager: AppManager,
    private val videoManager: VideoManager
) : ViewModel() {

    val permissionState = MutableLiveData<PermissionState>()

    enum class PermissionState {
        CHECKING,
        PERMISSION_OK,
        PERMISSION_KO
    }

    enum class ExtraUi {
        NONE,
        APPS,
        UNLOCK,
        TIMER
    }

    val extraUi = MutableLiveData<ExtraUi>()

    val loading = MutableLiveData<Boolean>()
    var videos = MutableLiveData<List<VideoElement>>()
    private val compositeDisposable = CompositeDisposable()

    init {
        val fileObserver = MyFileObserver(videoManager.sdCardPath)

        extraUi.postValue(ExtraUi.NONE)

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
        }, {
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
        compositeDisposable.add(

            rxPermissions
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
                }
        )

    }


    fun isPinSet(): Boolean {
        return sharedPreferencesManager.hasPin
    }

    fun showApps() {
        if (extraUi.value == ExtraUi.APPS) {
            extraUi.postValue(ExtraUi.NONE)
        } else {
            extraUi.postValue(ExtraUi.APPS)
        }
    }

    fun showSettings(context: Context) {

        if (isPinSet()) {
            if (extraUi.value != ExtraUi.UNLOCK) {
                extraUi.postValue(ExtraUi.UNLOCK)
            }
        } else {
            resetExtraUi()
            context.startActivity(Intent(context, SettingsActivity::class.java))
        }
    }

    fun showTimer() {
        if (extraUi.value == ExtraUi.TIMER) {
            extraUi.postValue(ExtraUi.NONE)
        } else {
            extraUi.postValue(ExtraUi.TIMER)
        }
    }

    fun onPlayVideo(video: VideoElement) {
        resetExtraUi()
    }

    fun resetExtraUi() {
        extraUi.postValue(ExtraUi.NONE)
    }

}