# Product Definition: Aura Player

## Vision
Aura Player is a modern, lightweight, and expressive Android local music player engineered with Kotlin, Jetpack Compose, and Jetpack Media3 (ExoPlayer). It bridges the gap between sleek visual elegance (Apple Music-level UI) and audiophile-grade controls using **Progressive Disclosure**—keeping the main interface clean and uncluttered while making deep audio features accessible through contextual bottom sheets, non-intrusive badges, and long-press interactions.

## Core Problem
Android local music players are divided into two extremes:
1. **Visually appealing but feature-deficient** apps lacking synced lyrics and audio processing.
2. **Audiophile apps** (e.g., Poweramp) that crowd the main interface with technical controls.

Aura Player solves this by hiding deep features inside intuitive, contextual layers without sacrificing audiophile capabilities.

## Development Environment & Constraints
- **Lightweight Setup:** VS Code + Gradle Command Line (`./gradlew assembleDebug` / `./gradlew installDebug`).
- **No Android Studio Required:** Pure CLI and lightweight editor workflow.
- **Deployment:** Physical Android device connected via ADB (USB Debugging / Wireless Debugging).

## Target Architecture & Feature Breakdown

### Phase 1: Core Player Engine (Immediate Focus)
- **Word-by-Word Synced Lyric Engine:**
  - Online query to LRCLIB API (`https://lrclib.net/api/get`).
  - Enhanced LRC / TTML parsing into word tokens with millisecond start/end timestamps.
  - Fluid 60/120Hz highlight animations synced to `ExoPlayer.currentPosition`.
  - Album art tap cross-fades into full-screen lyric canvas with manual offset slider ($\pm \text{ms}$).
- **ReplayGain Loudness Normalization:**
  - `jaudiotagger` extraction of `REPLAYGAIN_TRACK_GAIN` and `REPLAYGAIN_TRACK_PEAK`.
  - Linear scaling ($\text{scale} = 10^{\frac{\text{gain\_db}}{20}}$) applied via ExoPlayer audio scaling.
  - Subtle UI badge (e.g., `RG -2.1dB`) opening a ReplayGain adjustment sheet.
- **File Quality & Metadata Display:**
  - Sample rate, bitrate, and codec detection (`FLAC • 24-bit/96kHz`, `MP3 • 320kbps`).
  - Sleek chip display adjacent to metadata opening Audio Output / DSP sheet.

### Phase 2: Home Screen, Local Media Library Scanner & Settings (Completed)
- **Local Audio MediaStore Scanner & Room Persistence:**
  - Automatic background indexing of `.mp3`, `.flac`, `.m4a`, `.wav`, `.aac` files via `MediaStore`.
  - Room Database layer (`TrackEntity`, `AlbumEntity`, `ArtistEntity`) with reactive StateFlow streams.
- **Material 3 Expressive Home Screen & Mini-Player:**
  - Tabbed Dashboard (**Songs**, **Albums**, **Artists**) with quick-play hero card and real-time search.
  - Persistent bottom `MiniPlayer` with progress bar, play/pause toggle, and tap to expand.
- **Comprehensive Settings Screen:**
  - Appearance preferences (Theme Mode, Dynamic Monet Colors), Audio ReplayGain controls, and manual storage rescan trigger.

### Phase 3: Smart Features & Advanced Audio DSP
- **Offline Intelligent Autoplay Algorithm:**
  - On-device recommendation engine utilizing play history, genres, and BPM/mood tags to auto-extend queues.
- **Scrobbling Integration:**
  - Background hooks for Last.fm, ListenBrainz, and Open Scrobbler.
- **16-Band Parametric Equalizer:**
  - 16-band EQ with Bass Boost and Treble controls in swipe-up Audio Sheet.
