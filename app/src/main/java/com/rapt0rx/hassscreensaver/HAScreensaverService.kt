package com.rapt0rx.hassscreensaver

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.service.dreams.DreamService
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class HAScreensaverService : DreamService() {

    private var dashboard: WebView? = null
    private val handler = Handler(Looper.getMainLooper())
    private var currentUrl: String = ""

    private val retryLoad = Runnable {
        dashboard?.loadUrl(currentUrl)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = false   // any remote key press wakes the TV
        isFullscreen = true
        isScreenBright = true   // keep the dashboard at normal brightness

        currentUrl = Prefs.getUrl(this)
        if (currentUrl.isBlank()) {
            finish()
            return
        }

        val web = WebView(this).apply {
            setBackgroundColor(Color.BLACK)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true   // HA keeps its login token here
            settings.mediaPlaybackRequiresUserGesture = false
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            CookieManager.getInstance().setAcceptCookie(true)
            isFocusable = false
            isFocusableInTouchMode = false

            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                // Retry if the page can't load (e.g. network not up yet after wake)
                override fun onReceivedError(
                    view: WebView,
                    request: WebResourceRequest,
                    error: WebResourceError
                ) {
                    if (request.isForMainFrame) {
                        handler.removeCallbacks(retryLoad)
                        handler.postDelayed(retryLoad, RETRY_DELAY_MS)
                    }
                }
            }
        }

        dashboard = web
        setContentView(
            web,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        web.loadUrl(currentUrl)
    }

    override fun onDreamingStopped() {
        handler.removeCallbacks(retryLoad)
        dashboard?.apply {
            stopLoading()
            loadUrl("about:blank")
            removeAllViews()
            destroy()
        }
        dashboard = null
        super.onDreamingStopped()
    }

    private companion object {
        const val RETRY_DELAY_MS = 15_000L
    }
}
