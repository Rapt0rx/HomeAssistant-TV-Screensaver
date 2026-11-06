package com.rapt0rx.hassscreensaver

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.service.dreams.DreamService
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient

class HAScreensaverService : DreamService() {
    private var dashboard: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = false
        isFullscreen = true
        isScreenBright = false

        val prefs = getSharedPreferences("ha_prefs", Context.MODE_PRIVATE)
        val url = prefs.getString("ha_url", "http://homeassistant.local:8123")

        if (url.isNullOrBlank()) {
            finish()
            return
        }

        val web = WebView(this).apply {
            setBackgroundColor(Color.BLACK)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            settings.loadsImagesAutomatically = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            CookieManager.getInstance().setAcceptCookie(true)
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            isFocusable = false
            isFocusableInTouchMode = false
        }

        dashboard = web
        setContentView(
            web,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        web.loadUrl(url)
    }

    override fun onDreamingStopped() {
        dashboard?.apply {
            stopLoading()
            loadUrl("about:blank")
            removeAllViews()
            destroy()
        }
        dashboard = null
        super.onDreamingStopped()
    }
}
