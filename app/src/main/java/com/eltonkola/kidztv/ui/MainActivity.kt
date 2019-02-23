package com.eltonkola.kidztv.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.eltonkola.kidztv.R
import com.eltonkola.kidztv.ui.settings.SettingsActivity
import com.eltonkola.kidztv.utils.SpacesItemDecoration
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_apps.*
import kotlinx.android.synthetic.main.activity_main_lock.*
import kotlinx.android.synthetic.main.activity_main_sidebar.*
import kotlinx.android.synthetic.main.activity_main_timer_lock.*
import kotlinx.android.synthetic.main.activity_main_timer_settings.*
import kotlinx.android.synthetic.main.fragment_settings_pin.view.*
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
            root_apps.visibility = View.GONE
            root_lock.visibility = View.GONE
        }

        vm.videos.observe(this, Observer { videos ->
            (video_grid.adapter as VideoListAdapter).setVideoElements(videos)
        })

        vm.loading_apps.observe(this, Observer { loadingApps ->
            if(loadingApps){
                loading_apps.visibility = View.VISIBLE
            }else{
                loading_apps.visibility = View.GONE
            }
        })


        app_grid.layoutManager = GridLayoutManager(this, 6)
        app_grid.setHasFixedSize(true)

        app_grid.adapter = AppGridAdapter(this) { app ->
            startActivity(vm.openApp(app))
        }

        vm.apps.observe(this, Observer { apps ->
            if(apps.isEmpty()){
                no_apps.visibility = View.VISIBLE
            }else {
                no_apps.visibility = View.GONE
                (app_grid.adapter as AppGridAdapter).setData(apps)
            }
        })

        video_player.setOnPreparedListener {
            video_player.start()
        }

        otp_view.setOtpCompletionListener { otp ->

            if(otp != null && vm.isPinCorrect(otp)) {
                root_lock.visibility =  View.GONE
                otp_view.setText("")
                startActivity(Intent(this, SettingsActivity::class.java))
            }else {
                Toast.makeText(this@MainActivity, "Error $otp, is the wrong code", Toast.LENGTH_SHORT).show()
            }
        }


        but_timer.setOnClickListener {
            if (!vm.isPinSet()) {
                if (root_timer.visibility == View.VISIBLE) {
                    root_timer.visibility = View.GONE
                }else{
                    root_timer.visibility = View.VISIBLE
                }
            } else {
                if (root_timer.visibility == View.VISIBLE) {
                    root_timer.visibility = View.GONE
                    root_timer_lock.visibility = View.GONE
                } else {
                    if (root_timer_lock.visibility == View.VISIBLE) {
                        root_timer_lock.visibility = View.GONE
                        root_timer.visibility = View.GONE
                    } else {
                        root_timer_lock.visibility = View.VISIBLE
                    }
                }
            }
        }

        otp_view_timer.setOtpCompletionListener { otp ->

            if(otp != null && vm.isPinCorrect(otp)) {
                root_timer_lock.visibility =  View.GONE
                otp_view.setText("")
                root_timer.visibility =  View.VISIBLE
            }else {
                Toast.makeText(this@MainActivity, "Error $otp, is the wrong code", Toast.LENGTH_SHORT).show()
            }
        }


        but_settings.setOnClickListener {

                if(vm.isPinSet()){
                    if(root_lock.visibility == View.VISIBLE){
                        root_lock.visibility =  View.GONE
                    }else{
                        root_lock.visibility =  View.VISIBLE
                    }
                }else{
                    startActivity(Intent(this, SettingsActivity::class.java))

                }
        }

        but_apps.setOnClickListener {
            if(root_apps.visibility == View.VISIBLE){
                root_apps.visibility =  View.GONE
            }else{
                root_apps.visibility =  View.VISIBLE
            }
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

//        top_bar.visibility = View.VISIBLE

//        root_view.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        mVisible = true
    }

}
