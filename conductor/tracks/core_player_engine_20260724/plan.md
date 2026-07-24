# Implementation Plan - Phase 1: Core Player Engine

## Phase 1.1: Project Infrastructure & Base Android Architecture
- [x] Task: Scaffold base Android application structure (`com.auraplayer.app`) with Gradle Kotlin DSL (`build.gradle.kts`, `settings.gradle.kts`, `AndroidManifest.xml`). (bb27681)
- [x] Task: Configure Android 15 foreground service declarations (`android:foregroundServiceType="mediaPlayback"`), `POST_NOTIFICATIONS` runtime permissions, Media3, Jetpack Compose Material 3, Room, jaudiotagger, and Ktor/OkHttp dependencies. (a570549)
- [x] Task: Verify base project compilation with `./gradlew assembleDebug`. (5846293)
- [ ] Task: Phase Verification & Checkpoint (Refer to workflow.md)

## Phase 1.2: Core ExoPlayer & Media3 Service
- [ ] Task: Implement `PlaybackService` extending Media3 `MediaSessionService` with foreground service handling.
- [ ] Task: Create `PlayerManager` wrapping `ExoPlayer` instance with explicit `AudioAttributes` (`C.USAGE_MEDIA`, `C.AUDIO_CONTENT_TYPE_MUSIC`), `handleAudioFocus = true`, noisy audio receiver, state management, progress flows, and volume control.
- [ ] Task: Write unit tests for `PlayerManager` playback state changes, audio focus handling, and position reporting.
- [ ] Task: Build Compose UI main player screen (album art, title/artist display, playback controls, progress slider).
- [ ] Task: Phase Verification & Checkpoint (Refer to workflow.md)

## Phase 1.3: ReplayGain & Audio Metadata Extraction
- [ ] Task: Implement `MetadataExtractor` using `jaudiotagger` for sample rate, bitrate, codec, `REPLAYGAIN_TRACK_GAIN`, and `REPLAYGAIN_TRACK_PEAK`.
- [ ] Task: Write TDD unit tests for ReplayGain gain-to-scale calculation with peak clamping ($\text{scale}_{\text{final}} = \min\left(10^{\frac{\text{gain\_db}}{20}},\, \frac{1.0}{\text{peak}}\right)$) verifying anti-clipping when $\text{scale} \times \text{peak} > 1.0$.
- [ ] Task: Connect ReplayGain volume adjustments dynamically to ExoPlayer via `player.setVolume(scale)` to preserve hardware audio offload.
- [ ] Task: Render `AudioQualityChip` and `ReplayGainBadge` in Compose UI.
- [ ] Task: Phase Verification & Checkpoint (Refer to workflow.md)

## Phase 1.4: LRCLIB Word-by-Word Synced Lyric Engine
- [ ] Task: Implement 3-tier lyric resolution pipeline (`jaudiotagger` embedded `SYLT`/`USLT` tags $\rightarrow$ Room Database cache $\rightarrow$ `LrclibRepository` API).
- [ ] Task: Implement `LrcParser` for standard and enhanced (word-timestamped) LRC lyrics.
- [ ] Task: Write unit tests for `LrcParser` verifying millisecond parsing accuracy.
- [ ] Task: Build Room DB caching layer for LRCLIB responses and per-track lyric sync manual offset persistence ($\pm \text{ms}$).
- [ ] Task: Build Compose `LyricCanvas` with lambda-based state wrappers and `Modifier.drawWithContent` / `GraphicsLayer` animations for 60/120Hz rendering without recomposition jank, including manual offset adjustment slider linked to Room DB.
- [ ] Task: Connect album art tap cross-fade to `LyricCanvas`.
- [ ] Task: Phase Verification & Checkpoint (Refer to workflow.md)
