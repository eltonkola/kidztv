package com.eltonkola.kidstv.yourtubedownloader

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var vm: MainViewModel

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = resources.getString(R.string.app_name)


        vm = ViewModelProviders.of(this).get(MainViewModel::class.java)
        vm.permissionState.observe(this, Observer {
            when (it) {
                MainViewModel.PermissionState.CHECKING -> {
                }
                MainViewModel.PermissionState.PERMISSION_OK -> {
                }
                MainViewModel.PermissionState.PERMISSION_KO -> {
                    Toast.makeText(this, "No permissions, no fun!", Toast.LENGTH_LONG).show()
                    finish()
                }
            }

        })

        vm.checkPermissions(this)


        fab.setOnClickListener { view ->
            download()
        }

        webview.requestFocus(View.FOCUS_DOWN)
        webview.settings.javaScriptEnabled = true
        webview.settings.saveFormData = false
        webview.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webview.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY

        webview.webViewClient =
            object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, urlz: String): Boolean {
                    view.loadUrl(urlz)
                    return true // then it is not handled by default action
                }

                override fun onPageFinished(view: WebView, url: String) {
                    webview.scrollBy(0, 100)
                    super.onPageFinished(view, url)
                }
            }


        webview.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                if (newProgress == 100) {
                    loading_view.visibility = View.GONE
                    updateNavigation()
                } else {
                    loading_view.visibility = View.VISIBLE
                }
            }
        }
        webview.loadUrl("http://www.youtube.com/mobile")

    }

    fun download(){
        var urlLoad = webview.url
        urlLoad = urlLoad.replace("http://m.", "http://")
        urlLoad = urlLoad.replace("https://m.", "https://")
        urlLoad = urlLoad.replace("http://you", "http://www.you")
        urlLoad = urlLoad.replace("https://you", "https://www.you")

        val i = Intent(Intent.ACTION_SEND)
        i.setClass(this, DownloadActivity::class.java)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_TEXT, urlLoad)

        startActivity(i)
    }


    lateinit var butBack : MenuItem
    lateinit var butForward : MenuItem

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        butBack = menu[1]
        butForward = menu[2]

        return true
    }

    fun updateNavigation(){
        butBack.isEnabled = webview.canGoBack()
        butForward.isEnabled = webview.canGoForward()

        invalidateOptionsMenu()

    }

    override fun onBackPressed() {
        if(webview.canGoBack()){
            webview.goBack()
        }else{
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                finish()
                true
            }
            R.id.action_home -> {
                webview.loadUrl("http://www.youtube.com/mobile")
                true
            }
            R.id.action_back -> {
                if(webview.canGoBack()){
                    webview.goBack()
                }
                true
            }
            R.id.action_forward -> {
                if(webview.canGoForward()){
                    webview.goForward()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
