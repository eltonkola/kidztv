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

    val addOperation = MutableLiveData<EditState>()

    enum class EditState {
        ADD_OK, ADD_ERROR, REMOVE_OK, REMOVE_ERROR, IDLE
    }

    fun setOperationReflected(){
        addOperation.postValue(EditState.IDLE)
    }

    init {

        loading.postValue(true)
        compositeDisposable.add(appManager.getWhitelistedApps().subscribe({
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

    fun addApp(app: AppElement) {
        compositeDisposable.add(appManager.addAppToWhitelist(app).subscribe({
            addOperation.postValue(EditState.ADD_OK)
        }, {
            it.printStackTrace()
            addOperation.postValue(EditState.ADD_ERROR)
        }))
    }

    fun removeApp(app: AppElement) {
        compositeDisposable.add(appManager.removeAppFromWhitelist(app).subscribe({
            addOperation.postValue(EditState.REMOVE_OK)
        }, {
            addOperation.postValue(EditState.REMOVE_ERROR)
        }))

    }


}