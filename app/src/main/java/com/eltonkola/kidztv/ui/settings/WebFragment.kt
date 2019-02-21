package com.eltonkola.kidztv.ui.settings

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.Fragment
import com.eltonkola.kidztv.R
import kotlinx.android.synthetic.main.fragment_webview.*


class WebFragment : Fragment() {

    companion object {

        val URL = "url"
        val TITLE = "title"

        fun getFragment(title: String, url: String): WebFragment {
            val fragment = WebFragment()
            fragment.arguments = Bundle().apply {
                putString(TITLE, title)
                putString(URL, url)
            }
            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_webview, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        title.text = arguments?.getString(TITLE)
        val url = arguments?.getString(URL)

        webview.requestFocus(View.FOCUS_DOWN)
        webview.settings.javaScriptEnabled = true
        webview.settings.saveFormData = false
        webview.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webview.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY

        webview.webViewClient =
            object : WebViewClient() {
                @SuppressWarnings("deprecation")
                override fun shouldOverrideUrlLoading(view: WebView, urlz: String): Boolean {
                    if (url == urlz) {
                        view.loadUrl(urlz)
                    } else {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    }

                    return true
                }

                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    error_msg?.visibility = View.VISIBLE
                    super.onReceivedError(view, request, error)
                }

                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    loading_view.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView, url: String) {
                    webview.scrollBy(0, 100)
                    loading_view.visibility = View.GONE
                    error_msg.visibility = View.GONE
                    super.onPageFinished(view, url)
                }
            }

        webview.setWebChromeClient(object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                if (newProgress == 100) {
                    loading_view.visibility = View.GONE
                } else {
                    loading_view.visibility = View.VISIBLE
                }
            }
        })


        url?.let {
            webview.loadUrl(url)
        }

    }
}
