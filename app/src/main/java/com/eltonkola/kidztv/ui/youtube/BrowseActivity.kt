package com.eltonkola.kidztv.ui.youtube

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eltonkola.kidztv.R
import kotlinx.android.synthetic.main.activity_browse.*

class BrowseActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_browse)


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

        webview.loadUrl("http://www.youtube.com/mobile")

        downlaod.setOnClickListener {

            var urlLoad = webview.url
            urlLoad = urlLoad.replace("http://m.", "http://")
            urlLoad = urlLoad.replace("https://m.", "https://")
            urlLoad = urlLoad.replace("http://you", "http://www.you")
            urlLoad = urlLoad.replace("https://you", "https://www.you")

            val i = Intent(Intent.ACTION_SEND)
            i.setClass(this, DownloadActivity::class.java)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_TEXT, urlLoad)

            Toast.makeText(this, "urlLoad: $urlLoad", Toast.LENGTH_SHORT).show()

            startActivity(i)
        }

    }
}