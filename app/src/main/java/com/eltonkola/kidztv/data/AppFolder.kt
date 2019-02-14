package com.eltonkola.kidztv.data

import android.os.Environment
import java.io.File

class AppFolder{


    val externalVideoPath = "/kidztv/"

    private val videoFolder = File(Environment.getExternalStorageDirectory(),externalVideoPath)
    val sdCardPath : String get() = videoFolder.absolutePath


    init{
        if(!videoFolder.exists()){
            videoFolder.mkdirs()
        }
    }




}