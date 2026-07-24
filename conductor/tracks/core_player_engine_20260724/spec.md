# Track Specification: Phase 1 - Core Player Engine

## Overview
Initialize the Aura Player Android repository structure with Gradle Kotlin DSL, Jetpack Compose Material 3 Expressive UI, and AndroidX Media3 (ExoPlayer). Implement the core playback service, LRCLIB word-by-word synced lyric engine, ReplayGain normalization tag parser, and file quality metadata chip display.

## Functional Requirements
1. **Base Project & Gradle Setup:**
   - Android application module using Gradle Kotlin DSL (`build.gradle.kts`).
   - Configured for JDK 17, Min SDK 26, Target SDK 35, Compose Compiler/BOM.
   - Command-line build verification via `./gradlew assembleDebug`.
2. **Audio Playback Service (`Media3`):**
   - Android 15 compliant foreground service (`PlaybackService`) declaring `android:foregroundServiceType="mediaPlayback"` in `AndroidManifest.xml` with `POST_NOTIFICATIONS` runtime permissions.
   - Foreground `MediaSessionService` bound to ExoPlayer `1.5.+`.
   - Explicit `AudioAttributes` configuration (`C.USAGE_MEDIA`, `C.AUDIO_CONTENT_TYPE_MUSIC`) with `handleAudioFocus = true` and noisy audio (unplugged headphones) handling.
   - Media controls integration (play/pause/seek, track skip, notification center, lockscreen artwork, Bluetooth media keys).
3. **Word-by-Word Lyric Engine & Performance:**
   - **Lyric Resolution Priority Hierarchy:**
     1. Local file metadata tags (`jaudiotagger` embedded `SYLT`/`USLT` synced/unsynced lyrics).
     2. Cached LRCLIB responses stored in local Room Database for offline access.
     3. Remote `LrclibRepository` API (`https://lrclib.net/api/get`).
   - LRC / Enhanced LRC parser converting timestamps and word tokens into high-resolution timing models.
   - High-Refresh (60/120Hz) Rendering: Avoid passing raw player millisecond primitives into Composable parameters. Use lambda-based state wrappers and Compose `Modifier.drawWithContent` / `GraphicsLayer` offset animations to update word highlights without triggering full UI recompositions.
   - Manual Offset Persistence: Store user lyric offset adjustments ($\pm \text{ms}$) in Room Database per track ID.
   - Album art tap transition to lyric canvas and manual sync offset slider ($\pm \text{ms}$).
4. **ReplayGain Loudness Normalization:**
   - Tag extraction via `jaudiotagger` (`REPLAYGAIN_TRACK_GAIN` / `REPLAYGAIN_TRACK_PEAK`).
   - Peak-clamped linear scale calculation:
     $$\text{scale}_{\text{final}} = \min\left(10^{\frac{\text{gain\_db}}{20}},\, \frac{1.0}{\text{peak}}\right)$$
   - Offload Optimization: Apply initial ReplayGain volume scaling directly via `player.setVolume(scale)` rather than forcing a custom `AudioProcessor` in Phase 1 to preserve Android Hardware Audio Offload (battery efficiency).
   - Subtle UI badge (e.g., `RG -2.1dB`) opening a ReplayGain adjustment sheet.
5. **File Quality & Metadata Chip:**
   - Extracted sample rate, bit depth, bitrate, and container codec (`FLAC • 24-bit/96kHz`, `MP3 • 320kbps`).
   - Audio chip opening DSP & Output options bottom sheet.

## Acceptance Criteria
- App compiles successfully using `./gradlew assembleDebug` and installs via `./gradlew installDebug`.
- Background playback continues seamlessly when app is minimized or screen is locked with `mediaPlayback` service type and Audio Focus support.
- Word-by-word lyrics render smoothly at 60/120Hz using lambda wrappers and `drawWithContent` without UI recomposition jank, prioritizing embedded tags -> Room cache -> LRCLIB API, with user offset adjustments persisted in Room.
- ReplayGain tags dynamically adjust playback volume via `player.setVolume()` with strict peak clamping ($\text{scale} \times \text{peak} \le 1.0$) preserving hardware audio offload.
