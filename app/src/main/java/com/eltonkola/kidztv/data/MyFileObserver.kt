package com.eltonkola.kidztv.data

import android.os.FileObserver
import io.reactivex.subjects.PublishSubject

class MyFileObserver(path: String) : FileObserver(path, FileObserver.ALL_EVENTS) {

    private val subject = PublishSubject.create<String>().toSerialized()

    val observable = subject.doOnSubscribe { startWatching() }.doOnDispose { stopWatching() }.share()

    override fun onEvent(event: Int, path: String?) {
        path?.let { subject.onNext(it) }
    }
}