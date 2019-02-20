package com.eltonkola.kidztv.ui.settings.videomanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.eltonkola.kidztv.R
import com.eltonkola.kidztv.ui.ViewManagerViewModel
import kotlinx.android.synthetic.main.fragment_settings_video_manager.*
import kotlinx.android.synthetic.main.fragment_settings_video_manager.view.*

class VideoManagerFragment : Fragment() {

    lateinit var vm: ViewManagerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_settings_video_manager, container, false)

        rootView.item_detail.text = "Video Manager"

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm = ViewModelProviders.of(this).get(ViewManagerViewModel::class.java)

        vm.loading.observe(this, Observer { isLoading ->
            if (isLoading) {
                loading.visibility = View.VISIBLE
            } else {
                loading.visibility = View.GONE
            }
        })

        video_list.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        video_list.setHasFixedSize(true)

        video_list.adapter = EditVideoListAdapter(activity!!) { video ->
            vm.deleteFile(video)
        }

        vm.videos.observe(this, Observer { videos ->
            if (videos.isEmpty()) {
                no_videos.visibility = View.VISIBLE
            } else {
                no_videos.visibility = View.GONE
            }
            (video_list.adapter as EditVideoListAdapter).setVideoElements(videos)
        })

        vm.loadVideos()

    }
}
