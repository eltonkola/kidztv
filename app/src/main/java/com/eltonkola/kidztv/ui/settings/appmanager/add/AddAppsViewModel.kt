package com.eltonkola.kidztv.ui.settings.appmanager.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eltonkola.kidztv.data.AppManager
import com.eltonkola.kidztv.model.AppElement
import com.eltonkola.kidztv.model.VideoElement
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class AddAppsViewModel(private val appManager: AppManager) : ViewModel() {

    val loading = MutableLiveData<Boolean>()
    var apps = MutableLiveData<List<AppElement>>()
    private val compositeDisposable = CompositeDisposable()

    init {

        loading.postValue(true)
        compositeDisposable.add(appManager.getAddableApps().subscribe({
            loading.postValue(false)
            apps.postValue(it)
        }, {
            Timber.e(it)
            loading.postValue(false)
        }))

    }

    public override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

}