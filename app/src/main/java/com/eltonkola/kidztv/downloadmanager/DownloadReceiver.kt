package com.eltonkola.kidztv.downloadmanager


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class DownloadReceiver : BroadcastReceiver() {

    val DOWNLOAD_URL = "com.eltonkola.kidztv.DOWNLOAD_URL"
    val TITLE = "com.eltonkola.kidztv.TITLE"
    val FILE_NAME = "com.eltonkola.kidztv.FILE_NAME"

    override fun onReceive(context: Context, intent: Intent) {

        val downloadUrl = intent.getStringExtra(DOWNLOAD_URL)
        val title = intent.getStringExtra(TITLE)
        val fileName = intent.getStringExtra(FILE_NAME)
        if (downloadUrl != null && title != null && fileName != null) {
            context.startService(
                DownloadVideoService.getDownloadService(
                    context,
                    downloadUrl,
                    title,
                    fileName
                )
            )
        } else {
            Toast.makeText(context, "Not enough data to download the video!", Toast.LENGTH_SHORT).show()
        }


    }

}


