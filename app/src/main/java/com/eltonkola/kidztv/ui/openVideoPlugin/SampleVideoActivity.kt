package com.eltonkola.kidztv.ui.openVideoPlugin

import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.eltonkola.kidztv.R
import com.eltonkola.kidztv.ui.main.VideoListAdapter
import kotlinx.android.synthetic.main.activity_sample_video.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SampleVideoActivity : AppCompatActivity() {

    private val vm: SampleVideoViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        video_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        video_list.setHasFixedSize(true)

        video_list.adapter = VideoListAdapter(this) { video ->
            //todo - download it
        }

        vm.videos.observe(this, Observer { videos ->
            (video_list.adapter as VideoListAdapter).setVideoElements(videos)
        })


        vm.loading.observe(this, Observer { loading ->
            if (loading) {
                loading_videos.visibility = View.VISIBLE
            } else {
                loading_videos.visibility = View.GONE
            }
        })


    }

}
