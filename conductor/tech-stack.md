# Technology Stack: Aura Player

## Architectural Overview
- **Language & Runtime:** Kotlin (Targeting JDK 17 / 21)
- **UI Framework:** Jetpack Compose with Material 3 Expressive Design System
- **Audio Engine:** AndroidX Media3 (ExoPlayer `1.5.+`, `MediaSessionService`, `AudioProcessor`)
- **Lyrics Service:** LRCLIB API (`https://lrclib.net/api/get`), Custom Enhanced LRC / TTML Parser
- **Metadata & Audio Tagging:** `jaudiotagger` (for ReplayGain, codec, sample rate, bit depth, embedded artwork)
- **Database & Offline Cache:** Room Database (`androidx.room`) + DataStore Preferences
- **Asynchronous & Reactive Streams:** Kotlin Coroutines + StateFlow / SharedFlow

## Build Environment & Development Workflow
- **No Android Studio Required:** Command-line Gradle workflow (`./gradlew assembleDebug` / `./gradlew installDebug`) integrated with VS Code.
- **Deployment:** Physical Android device connected via ADB (USB / Wireless Debugging).
- **Target SDK:** 35 (Android 15), **Min SDK:** 26 (Android 8.0)
