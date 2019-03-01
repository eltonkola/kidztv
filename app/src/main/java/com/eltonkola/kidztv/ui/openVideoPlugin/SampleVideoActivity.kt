package com.eltonkola.kidztv.ui.openVideoPlugin

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eltonkola.kidztv.R
import kotlinx.android.synthetic.main.activity_sample_video.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class SampleVideoActivity : AppCompatActivity() {

    private val vm: SampleVideoViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_sample_video)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.open_videos_title)

        video_list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        video_list.setHasFixedSize(true)

        val dividerItemDecoration = DividerItemDecoration(this, RecyclerView.VERTICAL)
        video_list.addItemDecoration(dividerItemDecoration)

        video_list.adapter = OpenVideoListAdapter(this) { video ->
            if (video.alreadyDownloaded) {
                Toast.makeText(this, "This video is already downloaded!!", Toast.LENGTH_LONG).show()
            } else {
                downloadVideo(video)
            }
        }

        vm.videos.observe(this, Observer { videos ->
            (video_list.adapter as OpenVideoListAdapter).setOpenVideoElements(videos)
        })

        vm.error.observe(this, Observer { error ->
            if (error) {
                Toast.makeText(this, "Error loading videos!!", Toast.LENGTH_LONG).show()
                finish()
            }
        })


        vm.loading.observe(this, Observer { loading ->
            if (loading) {
                loading_videos.visibility = View.VISIBLE
            } else {
                loading_videos.visibility = View.GONE
            }
        })

        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onComplete)
    }

    var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context, intent: Intent) {
            vm.reloadVideos()
        }
    }

    val DOWNLOAD_URL = "com.eltonkola.kidztv.DOWNLOAD_URL"
    val TITLE = "com.eltonkola.kidztv.TITLE"
    val FILE_NAME = "com.eltonkola.kidztv.FILE_NAME"
    val ACTION = "com.eltonkola.kidztv.action.START_DOWNLOAD_SERVICE"

    fun downloadVideo(video: OpenVideoElement) {
        val intent = Intent(ACTION)
        intent.putExtra(DOWNLOAD_URL, video.getUrl())
        intent.putExtra(TITLE, video.title)
        intent.putExtra(FILE_NAME, video.getFileName())
        sendBroadcast(intent)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
