package com.rapt0rx.hassscreensaver

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.service.dreams.DreamService
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

class HAScreensaverService : DreamService() {

    private var webView: WebView? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = true
        isFullscreen = true
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onDreamingStarted() {
        super.onDreamingStarted()

        val prefs = getSharedPreferences("ha_prefs", Context.MODE_PRIVATE)
        val haUrl = prefs.getString("ha_url", "http://homeassistant.local:8123") ?: "http://homeassistant.local:8123"

        webView = WebView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                useWideViewPort = true
                loadWithOverviewMode = true
                mediaPlaybackRequiresUserGesture = false
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                cacheMode = WebSettings.LOAD_DEFAULT
                userAgentString = "Mozilla/5.0 (Linux; Android ${Build.VERSION.RELEASE}; Mi Box S) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36"
            }

            webViewClient = object : WebViewClient() {
                override fun onReceivedError(
                    view: WebView?,
                    errorCode: Int,
                    description: String?,
                    failingUrl: String?
                ) {
                    super.onReceivedError(view, errorCode, description, failingUrl)
                }
            }
        }

        setContentView(webView)
        webView?.loadUrl(haUrl)
    }

    override fun onDreamingStopped() {
        super.onDreamingStopped()
        webView?.apply {
            stopLoading()
            clearHistory()
            removeAllViews()
            destroy()
        }
        webView = null
    }
}
