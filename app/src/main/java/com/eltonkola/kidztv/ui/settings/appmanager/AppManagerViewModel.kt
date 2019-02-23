package com.eltonkola.kidztv.ui.settings.appmanager

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eltonkola.kidztv.data.AppManager
import com.eltonkola.kidztv.model.AppElement
import com.eltonkola.kidztv.model.VideoElement
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class AppManagerViewModel(private val appManager: AppManager) : ViewModel() {


    val loading = MutableLiveData<Boolean>()
    var apps = MutableLiveData<List<AppElement>>()
    private val compositeDisposable = CompositeDisposable()

    init {

        compositeDisposable.add(appManager.getWhitelistedApps().subscribe({

        }, {
            Timber.e(it)
        }))


    }


}