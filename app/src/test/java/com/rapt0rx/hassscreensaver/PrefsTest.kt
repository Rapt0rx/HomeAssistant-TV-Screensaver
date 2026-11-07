package com.rapt0rx.hassscreensaver

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PrefsTest {
    @Test fun acceptsDashboardAddresses() {
        listOf("http://homeassistant.local:8123/lovelace-tv/0", "https://ha.example.com/dashboard?kiosk=1#view", "http://192.168.1.10:8123", "http://[::1]:8123", "HTTPS://ha.example.com").forEach {
            assertTrue(it, Prefs.isValid(it))
        }
    }

    @Test fun rejectsMalformedOrUnsafeAddresses() {
        listOf("", "https://", "http:///dashboard", "file:///etc/passwd", "javascript:alert(1)", "https://bad host", "http://ha.local:0", "http://ha.local:65536", "https://user:password@ha.local", "https://ha.local:notaport").forEach {
            assertFalse(it, Prefs.isValid(it))
        }
    }
}
