package com.rapt0rx.hassscreensaver

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Interactive WebView used to sign in to Home Assistant. The screensaver runs in the
 * same app process and shares this WebView's storage, so once you are signed in here
 * the screensaver shows the dashboard without a login prompt.
 * Press Back when your dashboard is visible.
 */
class LoginActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PREVIEW = "preview"
    }

    private var web: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = WebView(this).apply {
            setBackgroundColor(Color.BLACK)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            CookieManager.getInstance().setAcceptCookie(true)
            webChromeClient = WebChromeClient()
            webViewClient = WebViewClient()
            isFocusable = true
            isFocusableInTouchMode = true
        }
        web = view
        setContentView(view)
        view.requestFocus()
        view.loadUrl(Prefs.getUrl(this))

        Toast.makeText(
            this,
            if (intent.getBooleanExtra(EXTRA_PREVIEW, false))
                "Dashboard preview. Press Back to return."
            else "Sign in, wait for the dashboard, then press Back",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onPause() {
        // Persist cookies / local storage so the screensaver sees the session
        CookieManager.getInstance().flush()
        super.onPause()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val view = web
        if (view != null && view.canGoBack()) view.goBack() else super.onBackPressed()
    }

    override fun onDestroy() {
        web?.destroy()
        web = null
        super.onDestroy()
    }
}
