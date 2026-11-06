# Build Fix Package

This package contains three files to add or replace in the repository root:

- `build.gradle` — removes the conflicting project-level repository declarations.
- `.github/workflows/build.yml` — builds a debug APK on pushes to `main` and from the Actions page.
- `app/proguard-rules.pro` — supplies the release build's referenced rules file.

After uploading these files to the matching paths in GitHub, the new workflow should start automatically. Its APK artifact is named `homeassistant-tv-screensaver-debug`.
