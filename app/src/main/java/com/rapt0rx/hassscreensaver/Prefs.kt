package com.rapt0rx.hassscreensaver

import android.content.Context
import java.net.URI

object Prefs {
    private const val FILE = "ha_prefs"
    private const val KEY_URL = "ha_url"
    const val DEFAULT_URL = "http://homeassistant.local:8123"

    fun getUrl(context: Context): String =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .getString(KEY_URL, DEFAULT_URL) ?: DEFAULT_URL

    fun setUrl(context: Context, url: String) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit().putString(KEY_URL, url).apply()
    }

    fun isValid(url: String): Boolean =
        try {
            val uri = URI(url)
            uri.scheme?.lowercase() in setOf("http", "https") &&
                !uri.host.isNullOrBlank() && uri.rawUserInfo == null &&
                (uri.port == -1 || uri.port in 1..65535)
        } catch (_: Exception) {
            false
        }
}
