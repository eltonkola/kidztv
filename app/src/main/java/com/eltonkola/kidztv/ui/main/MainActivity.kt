package com.eltonkola.kidztv.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
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
import com.eltonkola.kidztv.utils.SpacesItemDecoration
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

        vm.loading.observe(this, Observer { loading ->
            Toast.makeText(this, "Loading: $loading", Toast.LENGTH_SHORT).show()
        })

        video_grid.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        video_grid.setHasFixedSize(true)

        video_grid.addItemDecoration(
            SpacesItemDecoration(
                resources,
                R.dimen.horizontal_list_item_spacing,
                R.dimen.horizontal_list_item_top_margin,
                R.dimen.horizontal_list_item_bottom_margin,
                R.dimen.margin_start_end,
                R.dimen.margin_start_end
            )
        )

        video_grid.adapter = VideoListAdapter(this) { video ->
            video_player.setVideoURI(Uri.fromFile(video.file))
//            root_apps.visibility = View.GONE
//            root_lock.visibility = View.GONE
        }

        vm.videos.observe(this, Observer { videos ->
            (video_grid.adapter as VideoListAdapter).setVideoElements(videos)
        })


        //AppsFragment
        //LockFragment
        //TimerFragment

        video_player.setOnPreparedListener {
            video_player.start()
        }



        but_timer.setOnClickListener {
            //            if (!vm.isPinSet()) {
//                if (root_timer.visibility == View.VISIBLE) {
//                    root_timer.visibility = View.GONE
//                } else {
//                    root_timer.visibility = View.VISIBLE
//                }
//            } else {
//                if (root_timer.visibility == View.VISIBLE) {
//                    root_timer.visibility = View.GONE
//                    root_timer_lock.visibility = View.GONE
//                } else {
//                    if (root_timer_lock.visibility == View.VISIBLE) {
//                        root_timer_lock.visibility = View.GONE
//                        root_timer.visibility = View.GONE
//                    } else {
//                        root_timer_lock.visibility = View.VISIBLE
//                    }
//                }
//            }
        }


        but_settings.setOnClickListener {

//            if (vm.isPinSet()) {
//                if (root_lock.visibility == View.VISIBLE) {
//                    root_lock.visibility = View.GONE
//                } else {
//                    root_lock.visibility = View.VISIBLE
//                }
//            } else {
//                startActivity(Intent(this, SettingsActivity::class.java))
//
//            }
        }

        but_apps.setOnClickListener {
//            if (root_apps.visibility == View.VISIBLE) {
//                root_apps.visibility = View.GONE
//            } else {
//                root_apps.visibility = View.VISIBLE
//            }
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

    override fun onResume() {
        super.onResume()
        vm.reloadVideos()
    }

    //    fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
//        outAttrs.imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI
//
//        // etc.
//    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
//        hide()

//        hideSystemUI()
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
        if (mVisible) {
            hide()
        } else {
            show()
        }
        hideSystemUI()
    }

    private fun hide() {
        animateHideView(video_grid, 1)
        animateHideView(top_bar, -1)


        animateHideView(padding_left, 1)
        animateHideView(padding_right, -1)


//        top_bar.visibility = View.GONE

        mVisible = false
    }

    private fun animateHideView(view: View, per: Int) {
        view.animate()
            .translationY(per * view.height.toFloat())
            .alpha(0.0f)
            .setDuration(100)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    view.visibility = View.GONE
                }
            })
    }


    private fun animateHideShow(view: View) {
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

        animateHideShow(padding_left)
        animateHideShow(padding_right)

//        top_bar.visibility = View.VISIBLE

//        root_view.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        mVisible = true
    }

}
