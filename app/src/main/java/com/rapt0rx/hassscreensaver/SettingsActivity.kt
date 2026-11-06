package com.rapt0rx.hassscreensaver

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var urlInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pad = (48 * resources.displayMetrics.density).toInt()
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(pad, pad, pad, pad)
        }

        val title = TextView(this).apply {
            text = getString(R.string.settings_title)
            textSize = 28f
        }

        val help = TextView(this).apply {
            text = "Enter the full address of the dashboard to show, e.g.\n" +
                "https://yourname.duckdns.org:8123/lovelace-tv/0\n" +
                "(or an http://192.168.x.x:8123 address on your home network)\n\n" +
                "Then choose \"Save & sign in\" once so the screensaver is logged in. " +
                "Finally pick \"Home Assistant Screensaver\" under Settings > Device " +
                "Preferences > Screen saver."
            textSize = 16f
            setTextColor(Color.GRAY)
            setPadding(0, pad / 4, 0, pad / 2)
        }

        urlInput = EditText(this).apply {
            hint = "https://yourname.duckdns.org:8123"
            setText(Prefs.getUrl(this@SettingsActivity))
            setSingleLine(true)
        }

        val saveButton = Button(this).apply {
            text = "Save"
            setOnClickListener { if (saveUrl()) finish() }
        }

        val loginButton = Button(this).apply {
            text = "Save & sign in"
            setOnClickListener {
                if (saveUrl()) startActivity(Intent(this@SettingsActivity, LoginActivity::class.java))
            }
        }

        layout.addView(title)
        layout.addView(help)
        layout.addView(urlInput)
        layout.addView(saveButton)
        layout.addView(loginButton)
        setContentView(layout)

        urlInput.requestFocus()
    }

    private fun saveUrl(): Boolean {
        val input = urlInput.text.toString().trim()
        return if (Prefs.isValid(input)) {
            Prefs.setUrl(this, input)
            Toast.makeText(this, "URL saved", Toast.LENGTH_SHORT).show()
            true
        } else {
            Toast.makeText(this, "Enter a valid http:// or https:// URL", Toast.LENGTH_LONG).show()
            false
        }
    }
}
