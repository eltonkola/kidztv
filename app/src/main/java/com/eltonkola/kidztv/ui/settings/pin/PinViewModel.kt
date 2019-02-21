package com.eltonkola.kidztv.ui.settings.pin

import androidx.lifecycle.ViewModel
import com.eltonkola.kidztv.data.AppManager

class PinViewModel(val repo: AppManager) : ViewModel() {

    fun sayHello() = "aaa from $this"
}