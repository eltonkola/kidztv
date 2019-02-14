package com.eltonkola.kidztv.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.eltonkola.kidztv.R
import com.eltonkola.kidztv.ui.youtube.BrowseActivity
import com.eltonkola.kidztv.utils.SpacesItemDecoration
import kotlinx.android.synthetic.main.activity_main.*
import android.animation.Animator
import android.animation.AnimatorListenerAdapter




class MainActivity : AppCompatActivity() {

    private var mVisible: Boolean = true

    lateinit var vm: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION


        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        video_player.setOnClickListener { toggle() }

        vm = ViewModelProviders.of(this).get(MainViewModel::class.java)

        vm.loading.observe(this, Observer { loading ->
            Toast.makeText(this, "Loading: $loading", Toast.LENGTH_SHORT).show()
        })

        video_grid.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        video_grid.setHasFixedSize(true)

        video_grid.addItemDecoration(SpacesItemDecoration( resources, R.dimen.horizontal_list_item_spacing, R.dimen.horizontal_list_item_top_margin, R.dimen.horizontal_list_item_bottom_margin, R.dimen.margin_start_end, R.dimen.margin_start_end))

        video_grid.adapter = VideoListAdapter(this) { video ->
            video_player.setVideoURI(Uri.fromFile(video.file))
        }

        vm.videos.observe(this, Observer { videos ->
            (video_grid.adapter as VideoListAdapter).setVideoElements(videos)
        })

        video_player.setOnPreparedListener {
            video_player.start()
        }

        but_settings.setOnClickListener {
            startActivity(Intent(this, BrowseActivity::class.java))
        }

        vm.permissionState.observe(this, Observer {
            when (it) {
                MainViewModel.PermissionState.CHECKING -> {
                }
                MainViewModel.PermissionState.PERMISSION_OK -> {
                    vm.loadVideos()
                }
                MainViewModel.PermissionState.PERMISSION_KO -> {
                    Toast.makeText(this, "No permissions no fun!", Toast.LENGTH_LONG).show()
                    finish()
                }
            }

        })

        vm.checkPermissions(this)
    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
//        hide()
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        animateHideView(video_grid, 1)
        animateHideView(top_bar, -1)
//        top_bar.visibility = View.GONE

        mVisible = false
    }

    private fun animateHideView(view: View, per: Int){
        view.animate()
            .translationY(per* view.height.toFloat())
            .alpha(0.0f)
            .setDuration(100)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    view.visibility = View.GONE
                }
            })
    }


    private fun animateHideShow(view: View){
        view.visibility = View.VISIBLE
        view.animate()
            .translationY(0.toFloat())
            .alpha(1.0f)
            .setDuration(100)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)

                }
            })
    }


    private fun show() {
        animateHideShow(video_grid)
        animateHideShow(top_bar)

//        top_bar.visibility = View.VISIBLE

//        root_view.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        mVisible = true
    }

}
