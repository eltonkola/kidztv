package com.eltonkola.kidstv.yourtubedownloader

import android.Manifest
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable

class MainViewModel : ViewModel() {

    val permissionState = MutableLiveData<PermissionState>()

    enum class PermissionState {
        CHECKING,
        PERMISSION_OK,
        PERMISSION_KO
    }

    private val compositeDisposable = CompositeDisposable()

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