package com.eltonkola.kidztv.ui.settings.plugins

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.eltonkola.kidztv.R
import kotlinx.android.synthetic.main.fragment_settings_video_manager.view.*

class DownloadPluginsFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_settings_pin, container, false)

        rootView.item_detail.text = "Download plugins"

        return rootView
    }

}
