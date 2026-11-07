# Home Assistant TV Screensaver

Display a Home Assistant dashboard as your Android TV / Google TV screensaver.
The dashboard fills the screen at normal brightness. Press any remote key to exit
and return to your TV. Android 9 (API 28) or newer is required.

## Install and configure

1. In this repository’s **Actions** tab, open a successful **Build Android TV APK**
   run and download the `homeassistant-tv-screensaver-debug` artifact. Extract
   `app-debug.apk` and sideload it onto your TV (for example, with
   `adb install -r app-debug.apk`). This is a development build.
2. Open **HA TV Screensaver** from your TV’s apps.
3. Enter the full dashboard URL, including its path, such as
   `http://192.168.1.100:8123/lovelace-tv/0`. The TV must be able to reach this address.
4. Choose **Save & sign in**, sign in to Home Assistant, and wait for your dashboard.
   Press Back to return; if it navigates browser history first, press Back again.
5. Use **Preview dashboard** to check the saved address and session. This opens an
   interactive preview; the actual screensaver exits on any remote key.
6. Choose **Open screensaver settings**, select **Home Assistant Screensaver**, and
   set your preferred start delay. Menu names vary by TV manufacturer.

The sign-in page and screensaver share the app’s WebView storage. Clearing app
storage or uninstalling the app removes the saved URL and session. Home Assistant
can also expire or revoke a session; sign in again from the app when needed.

## Dashboard tips

Use a dashboard designed for viewing across a room, with large text and only the
information you need. A dedicated non-admin Home Assistant account is recommended
for shared TVs. Prefer HTTPS when available: HTTP sends login and dashboard traffic
without transport encryption. This app does not bypass invalid TLS certificates.
Normal brightness and static cards can cause image retention on some displays;
choose your dashboard and TV settings accordingly.

## Connection recovery

If the main dashboard request fails because of a network or HTTP error, the app
shows a small status message and retries after 5 seconds, then increases the delay
to a maximum of 60 seconds. A successful load resets the delay and hides the message.
Remote keys still exit while reconnecting. Home Assistant’s frontend manages its own
live connection after loading; the app does not periodically reload a healthy page.

## Troubleshooting

- **Cannot connect:** check the URL in a browser on the same network as the TV.
  If `homeassistant.local` does not resolve on the TV, try your server’s LAN IP.
- **Login page appears:** open the app and choose **Save & sign in** again.
- **Preview works but screensaver does not start:** verify the selected screensaver
  and idle delay in TV settings. Some devices restrict third-party screensavers.
- **Certificate error:** use a trusted HTTPS certificate; certificate checks remain enabled.

## Development

The project uses Android SDK 34, Gradle 8.7, and JDK 17. With these installed and
`ANDROID_HOME` configured, run:

```sh
gradle :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/app-debug.apk`.
