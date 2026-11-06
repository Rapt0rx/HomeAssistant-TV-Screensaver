package com.rapt0rx.hassscreensaver

import android.content.Context

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
        url.startsWith("http://") || url.startsWith("https://")
}
