package com.rapt0rx.hassscreensaver

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.service.dreams.DreamService
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.TextView

class HAScreensaverService : DreamService() {
    private var dashboard: WebView? = null
    private var status: TextView? = null
    private val handler = Handler(Looper.getMainLooper())
    private var currentUrl = ""
    private var retryDelay = INITIAL_RETRY_MS
    private var loadFailed = false
    private var active = false

    private val retryLoad = Runnable {
        if (active) dashboard?.loadUrl(currentUrl)
    }

    private fun scheduleRetry() {
        if (!active) return
        loadFailed = true
        status?.apply {
            text = getString(R.string.connection_retry, retryDelay / 1000)
            visibility = View.VISIBLE
        }
        handler.removeCallbacks(retryLoad)
        handler.postDelayed(retryLoad, retryDelay)
        retryDelay = (retryDelay * 2).coerceAtMost(MAX_RETRY_MS)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = false // Any remote key exits the screensaver.
        isFullscreen = true
        isScreenBright = true
        currentUrl = Prefs.getUrl(this)
        if (!Prefs.isValid(currentUrl)) {
            finish()
            return
        }
        active = true
        retryDelay = INITIAL_RETRY_MS
        val web = WebView(this).apply {
            setBackgroundColor(Color.BLACK)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            CookieManager.getInstance().setAcceptCookie(true)
            isFocusable = false
            isFocusableInTouchMode = false
            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView, url: String, favicon: android.graphics.Bitmap?) {
                    loadFailed = false
                    status?.apply {
                        text = getString(R.string.connecting)
                        visibility = View.VISIBLE
                    }
                }

                override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                    if (request.isForMainFrame) scheduleRetry()
                }

                override fun onReceivedHttpError(view: WebView, request: WebResourceRequest, errorResponse: WebResourceResponse) {
                    if (request.isForMainFrame) scheduleRetry()
                }

                override fun onPageFinished(view: WebView, url: String) {
                    if (active && !loadFailed) {
                        handler.removeCallbacks(retryLoad)
                        retryDelay = INITIAL_RETRY_MS
                        status?.visibility = View.GONE
                        CookieManager.getInstance().flush()
                    }
                }
            }
        }
        dashboard = web
        val container = FrameLayout(this)
        container.addView(web, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        status = TextView(this).apply {
            text = getString(R.string.connecting)
            setTextColor(Color.WHITE)
            setBackgroundColor(0xCC000000.toInt())
            val padding = (16 * resources.displayMetrics.density).toInt()
            setPadding(padding, padding, padding, padding)
        }
        container.addView(status, FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL))
        setContentView(container)
        web.loadUrl(currentUrl)
    }

    private fun releaseDashboard() {
        active = false
        handler.removeCallbacksAndMessages(null)
        dashboard?.apply {
            stopLoading()
            (parent as? ViewGroup)?.removeView(this)
            removeAllViews()
            destroy()
        }
        dashboard = null
        status = null
    }

    override fun onDreamingStopped() {
        releaseDashboard()
        super.onDreamingStopped()
    }

    override fun onDetachedFromWindow() {
        releaseDashboard()
        super.onDetachedFromWindow()
    }

    private companion object {
        const val INITIAL_RETRY_MS = 5_000L
        const val MAX_RETRY_MS = 60_000L
    }
}
