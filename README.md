# Home Assistant TV Screensaver

An Android TV screensaver prototype that displays a Home Assistant dashboard in a full-screen WebView.

## Target device

- Xiaomi Mi Box S running Android 9 (API 28)
- Minimum supported Android version: Android 9 (API 28)

## Build an APK

GitHub Actions builds a debug APK when changes are pushed to `main`, or when manually started.

1. Open the repository's **Actions** tab.
2. Select the latest **Build Android TV APK** run.
3. Wait for the build to finish successfully.
4. Download the `homeassistant-tv-screensaver-debug` artifact.
5. Extract the downloaded ZIP to find the APK.

## Configure on the Mi Box

1. Install and open the app.
2. Enter your Home Assistant URL in the settings screen.
3. Save the URL.
4. Start the screensaver and complete Home Assistant sign-in in its WebView if prompted.
5. In Android TV screensaver / Daydream settings, select **Home Assistant Screensaver** if the Mi Box firmware lists third-party dreams.

## Security and limitations

- Do not commit Home Assistant passwords, access tokens, or private URLs to this public repository.
- The URL is stored in the app's private preferences on the TV.
- This is an early prototype and still needs testing on the actual Mi Box S.
- Some Android TV firmware versions hide or restrict third-party DreamService screensavers.
- The app uses the normal Home Assistant web sign-in; credentials are not embedded in source code.
