package com.eltonkola.kidztv.ui.main

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.eltonkola.kidztv.R
import com.eltonkola.kidztv.ui.main.apps.AppsFragment
import com.eltonkola.kidztv.ui.main.lock.LockFragment
import com.eltonkola.kidztv.ui.main.timer.TimerFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_topbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private var mVisible: Boolean = true

    private val vm: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION


        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        video_player.setOnClickListener { toggle() }

        video_grid.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        video_grid.setHasFixedSize(true)

//        video_grid.addItemDecoration(
//            SpacesItemDecoration(
//                resources,
//                R.dimen.horizontal_list_item_spacing,
//                R.dimen.horizontal_list_item_top_margin,
//                R.dimen.horizontal_list_item_bottom_margin,
//                R.dimen.margin_start_end,
//                R.dimen.margin_start_end
//            )
//        )

        video_grid.adapter = VideoListAdapter(this) { video ->
            video_player.setVideoURI(Uri.fromFile(video.file))
            vm.onPlayVideo(video)
            hide()
        }

        vm.videos.observe(this, Observer { videos ->
            (video_grid.adapter as VideoListAdapter).setVideoElements(videos)
        })


        vm.loading.observe(this, Observer { loading ->
            if (loading) {
                loading_videos.visibility = View.VISIBLE
            } else {
                loading_videos.visibility = View.GONE
            }
        })




        vm.extraUi.observe(this, Observer { ui ->
            showExtraUi(ui)
        })

        video_player.setOnPreparedListener {
            video_player.start()
        }

        but_timer.setOnClickListener {
            vm.showTimer()
        }


        but_settings.setOnClickListener {
            vm.showSettings(this)
        }

        but_apps.setOnClickListener {
            vm.showApps()
        }

        vm.permissionState.observe(this, Observer {
            when (it) {
                MainViewModel.PermissionState.CHECKING -> {
                }
                MainViewModel.PermissionState.PERMISSION_OK -> {
                    vm.reloadVideos()
                }
                MainViewModel.PermissionState.PERMISSION_KO -> {
                    Toast.makeText(this, "No permissions no fun!", Toast.LENGTH_LONG).show()
                    finish()
                }
            }

        })

        vm.checkPermissions(this)


    }

    private fun showExtraUi(ui: MainViewModel.ExtraUi) {
        when (ui) {
            MainViewModel.ExtraUi.NONE -> {
                for (fragment in supportFragmentManager.fragments) {
                    supportFragmentManager.beginTransaction().remove(fragment).commit()
                }
                extra_ui_container.visibility = View.GONE
            }
            MainViewModel.ExtraUi.APPS -> {
                extra_ui_container.visibility = View.VISIBLE
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.extra_ui_container, AppsFragment())
                    .commit()
            }
            MainViewModel.ExtraUi.UNLOCK -> {
                extra_ui_container.visibility = View.VISIBLE
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.extra_ui_container, LockFragment())
                    .commit()
            }
            MainViewModel.ExtraUi.TIMER -> {
                extra_ui_container.visibility = View.VISIBLE
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.extra_ui_container, TimerFragment())
                    .commit()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        vm.reloadVideos()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun toggle() {
        if (root_view.progress == 0.0f) {
            hide()
        } else {
            show()
        }
        hideSystemUI()
    }


    private fun hide() {
        root_view.transitionToEnd()
        extra_ui_container.visibility = View.GONE
        mVisible = false
    }


    private fun show() {
        root_view.transitionToStart()
        extra_ui_container.visibility = View.VISIBLE
        root_view.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        mVisible = true
    }

    fun resetExtraUi() {
        vm.resetExtraUi()
    }

}
