package com.eltonkola.kidztv.ui.main.apps

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eltonkola.kidztv.data.AppManager
import com.eltonkola.kidztv.model.AppElement
import io.reactivex.disposables.CompositeDisposable

class AppsViewModel(private val appManager: AppManager) : ViewModel() {


    val loading_apps = MutableLiveData<Boolean>()
    var apps = MutableLiveData<List<AppElement>>()

    private val compositeDisposable = CompositeDisposable()

    init {

        loading_apps.postValue(true)

        compositeDisposable.add(appManager.getWhitelistedApps().subscribe({
            loading_apps.postValue(false)
            apps.postValue(it)
        }, {
            loading_apps.postValue(false)
        }))

    }

    public override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }


    fun openApp(app: AppElement): Intent {
        return appManager.getIntentForPackage(app.packageName)
    }
}