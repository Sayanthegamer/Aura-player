# Implementation Plan: Phase 3 - Smart Features & Advanced Audio DSP

## Phase 1: 16-Band Parametric Equalizer & Native Audio DSP Sheet
- [x] Task: Equalizer & DSP Core Engine
  - [x] Implement `AudioDspManager` utilizing `android.media.audiofx.DynamicsProcessing` attached to `ExoPlayer.audioSessionId`.
  - [x] Implement 16 parametric band configuration (gain, center frequency, Q-factor) with default flat presets and standard profiles (Rock, Jazz, Bass Boost).
  - [x] Implement low-shelf (Bass Boost), high-shelf (Treble), and peak limiter controls.
  - [x] Implement try-catch fallback to standard Android 5-band `Equalizer` for low-end OEM devices unsupported by `DynamicsProcessing`.
  - [x] Manage hardware offload state switching when EQ is enabled vs disabled.
  - [x] Persist EQ state & band gains in `DataStore`.
- [x] Task: Audio & DSP Bottom Sheet UI
  - [x] Create Material 3 `AudioDspBottomSheet` with tabbed/scrolling band sliders, preset dropdown, and bypass switch.
  - [x] Connect sheet UI to `AudioDspManager` state via StateFlow.
- [x] Task: Unit & Manual Tests for Audio DSP
  - [x] Write `AudioDspManagerTest` for band gain calculation and preset loading logic.
- [x] Task: Phase 1 Verification & Checkpoint (Refer to workflow.md)

## Phase 2: Offline Intelligent Autoplay Algorithm
- [x] Task: Room Database Scoring Extensions
  - [x] Update Room schema (`TrackEntity`) to support BPM, mood/energy tags, and play history timestamps.
  - [x] Create `AutoplayScorer` implementing content filtering & recency penalty math:
        $$\text{Score}(T) = (w_g \cdot S_{\text{genre}}) + (w_a \cdot S_{\text{artist}}) + (w_b \cdot S_{\text{bpm}}) + (w_m \cdot S_{\text{mood}}) - P_{\text{recency}}$$
- [x] Task: Pre-fetching Queue Integration
  - [x] Connect `PlayerManager` queue monitoring to calculate and enqueue candidate tracks on `Dispatchers.IO` when queue length drops below 2.
  - [x] Implement fallback hierarchy (High Score match -> Same Genre match -> Favorite non-recent track).
  - [x] Persist `"Autoplay Similar Tracks"` toggle setting in `DataStore` and integrate toggle in Queue UI.
- [x] Task: Unit Tests for Autoplay Scorer
  - [x] Write `AutoplayScorerTest` verifying scoring weights, recency decay penalties, and zero-candidate fallback handling.
- [x] Task: Phase 2 Verification & Checkpoint (Refer to workflow.md)

## Phase 3: Background Scrobbling Infrastructure
- [x] Task: Scrobble Queue Persistence & Threshold Monitor
  - [x] Create Room `ScrobbleQueueEntity` (`artist`, `track`, `album`, `timestamp`, `duration`, `status`).
  - [x] Add playback listener in `PlayerManager` tracking 50% / 240s scrobble validation criteria.
- [x] Task: WorkManager Batch Sync Engine
  - [x] Implement `ScrobbleWorker` with network constraint (`NetworkType.CONNECTED`), battery constraint (`setRequiresBatteryNotLow(true)`), and exponential backoff retry.
  - [x] Create `ScrobbleProvider` interface with API implementations for Last.fm (Session Key Auth), ListenBrainz (Token Auth), and Open Scrobbler.
  - [x] Batch send pending Room entries on network connection.
- [x] Task: Settings & Auth UI
  - [x] Implement Settings section for `"Scrobbling & Statistics"` with service login/token prompts and status indicators.
- [x] Task: Unit Tests for Scrobbler
  - [x] Write `ScrobbleThresholdTest` and `ScrobbleWorkerTest` using mock network client.
- [x] Task: Phase 3 Verification & Checkpoint (Refer to workflow.md)
