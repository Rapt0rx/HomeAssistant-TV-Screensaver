package com.rapt0rx.hassscreensaver

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        val urlInput = EditText(this).apply {
            hint = "http://192.168.1.100:8123"
            val prefs = getSharedPreferences("ha_prefs", Context.MODE_PRIVATE)
            setText(prefs.getString("ha_url", "http://homeassistant.local:8123"))
            setSingleLine(true)
        }

        val saveButton = Button(this).apply {
            text = "Save URL"
            setOnClickListener {
                val inputUrl = urlInput.text.toString().trim()
                if (inputUrl.isNotEmpty() && (inputUrl.startsWith("http://") || inputUrl.startsWith("https://"))) {
                    getSharedPreferences("ha_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .putString("ha_url", inputUrl)
                        .apply()
                    Toast.makeText(this@SettingsActivity, "URL Saved", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@SettingsActivity, "Enter a valid http:// or https:// URL", Toast.LENGTH_LONG).show()
                }
            }
        }

        layout.addView(urlInput)
        layout.addView(saveButton)
        setContentView(layout)
    }
}
