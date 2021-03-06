package com.eltonkola.kidztv.downloadmanager

import android.app.DownloadManager
import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.eltonkola.kidztv.data.AppFolder

class DownloadVideoService : IntentService("DownloadVideoService") {

    private val externalVideoPath: String get() = AppFolder().externalVideoPath

    override fun onHandleIntent(intent: Intent?) {
        val downloadPath = intent!!.getStringExtra(DOWNLOAD_PATH)
        val downloadTitle = intent!!.getStringExtra(DOWNLOAD_TITLE)
        val fileName = intent!!.getStringExtra(DOWNLOAD_FILE_NAME)
        startDownload(downloadPath, downloadTitle, fileName)
    }

    private fun startDownload(downloadPath: String, downloadTitle: String, fileName: String) {
        val uri = Uri.parse(downloadPath) // Path where you want to download file.
        val request = DownloadManager.Request(uri)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)  // Tell on which network you want to download file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)  // This will show notification on top when downloading the file.
        request.setTitle("Downloading $downloadTitle") // Title for notification.
        request.setVisibleInDownloadsUi(true)
        request.setDestinationInExternalPublicDir(externalVideoPath, fileName)  // Storage directory path
        (getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request) // This will start downloading
    }

    companion object {
        private val DOWNLOAD_PATH = "DOWNLOAD_PATH"
        private val DOWNLOAD_TITLE = "DOWNLOAD_TITLE"
        private val DOWNLOAD_FILE_NAME = "DOWNLOAD_FILE_NAME"

        fun getDownloadService(
            callingClassContext: Context, downloadPath: String,
            downloadTitle: String, fileName: String
        ): Intent {
            return Intent(callingClassContext, DownloadVideoService::class.java)
                .putExtra(DOWNLOAD_PATH, downloadPath)
                .putExtra(DOWNLOAD_TITLE, downloadTitle)
                .putExtra(DOWNLOAD_FILE_NAME, fileName)
        }
    }
}