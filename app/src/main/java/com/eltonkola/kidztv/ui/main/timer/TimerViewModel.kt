package com.eltonkola.kidztv.ui.main.timer

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eltonkola.kidztv.data.AppManager
import com.eltonkola.kidztv.data.SharedPreferencesManager
import com.eltonkola.kidztv.model.AppElement
import io.reactivex.disposables.CompositeDisposable

class TimerViewModel(private val sharedPreferencesManager: SharedPreferencesManager) : ViewModel() {


    private val compositeDisposable = CompositeDisposable()

    init {



    }

    public override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }


    fun isPinCorrect(otp: String): Boolean {
        return sharedPreferencesManager.getPin().toString() == otp
    }

    fun isPinSet(): Boolean {
        return sharedPreferencesManager.hasPin
    }

}