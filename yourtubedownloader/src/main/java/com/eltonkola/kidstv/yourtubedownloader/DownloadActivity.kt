package com.eltonkola.kidstv.yourtubedownloader

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile


class DownloadActivity : Activity() {

    private var mainLayout: LinearLayout? = null
    private var mainProgressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_download)
        mainLayout = findViewById<View>(R.id.main_layout) as LinearLayout
        mainProgressBar = findViewById<View>(R.id.prgrBar) as ProgressBar

        // Check how it was started and if we can get the youtube link
        if (savedInstanceState == null && Intent.ACTION_SEND == intent.action
            && intent.type != null && "text/plain" == intent.type
        ) {

            val ytLink = intent.getStringExtra(Intent.EXTRA_TEXT)

            if (ytLink != null && (ytLink.contains("://youtu.be/") || ytLink.contains("youtube.com/watch?v="))) {
                youtubeLink = ytLink
                // We have a valid link
                getYoutubeDownloadUrl(youtubeLink!!)
            } else {
                Toast.makeText(this, "Invalid link $ytLink", Toast.LENGTH_LONG).show()
                finish()
            }
        } else if (savedInstanceState != null && youtubeLink != null) {
            getYoutubeDownloadUrl(youtubeLink!!)
        } else {
            finish()
        }
    }

    private fun getYoutubeDownloadUrl(youtubeLink: String) {

        val extractor = object : YouTubeExtractor(this) {

            override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, vMeta: VideoMeta) {
                mainProgressBar!!.visibility = View.GONE

                if (ytFiles == null) {
                    // Something went wrong we got no urls. Always check this.
                    finish()
                    return
                }
                // Iterate over itags
                var i = 0
                var itag: Int
                while (i < ytFiles.size()) {
                    itag = ytFiles.keyAt(i)
                    // ytFile represents one file with its url and meta data
                    val ytFile = ytFiles.get(itag)

                    // Just add videos in a decent format => height -1 = audio
                    if (ytFile.format.height == -1 || ytFile.format.height >= 360) {
                        addButtonToMainLayout(vMeta.title, ytFile)
                    }
                    i++
                }
            }
        }

        extractor.extract(youtubeLink, true, false)
    }

    private fun addButtonToMainLayout(videoTitle: String, ytfile: YtFile) {
        // Display some buttons and let the user choose the format
        var btnText = if (ytfile.format.height == -1)
            "Audio " +
                    ytfile.format.audioBitrate + " kbit/s"
        else
            ytfile.format.height.toString() + "p"
        btnText += if (ytfile.format.isDashContainer) " dash" else ""
        val btn = Button(this)
        btn.text = btnText
        btn.setOnClickListener {
            var filename: String
            if (videoTitle.length > 55) {
                filename = videoTitle.substring(0, 55) + "." + ytfile.format.ext
            } else {
                filename = videoTitle + "." + ytfile.format.ext
            }
            filename = filename.replace("[\\\\><\"|*?%:#/]".toRegex(), "")
            downloadFromUrl(ytfile.url, videoTitle, filename)
            finish()
        }
        mainLayout!!.addView(btn)
    }

    val DOWNLOAD_URL = "com.eltonkola.kidztv.DOWNLOAD_URL"
    val TITLE = "com.eltonkola.kidztv.TITLE"
    val FILE_NAME = "com.eltonkola.kidztv.FILE_NAME"

    val ACTION = "com.eltonkola.kidztv.action.START_DOWNLOAD_SERVICE"

    private fun downloadFromUrl(youtubeDlUrl: String, downloadTitle: String, fileName: String) {
        val intent = Intent(ACTION)
        intent.putExtra(DOWNLOAD_URL, youtubeDlUrl)
        intent.putExtra(TITLE, downloadTitle)
        intent.putExtra(FILE_NAME, fileName)
        sendBroadcast(intent)
    }

    companion object {

        private var youtubeLink: String? = null
    }

}
