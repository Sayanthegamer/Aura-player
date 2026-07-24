# Track Specification: Phase 3 - Smart Features & Advanced Audio DSP

## Overview
Phase 3 elevates Aura Player into a high-performance audio engine and intelligent player. It introduces a native 16-band Parametric Equalizer with DSP controls (Bass/Treble boost, Limiter), an offline Room-backed Intelligent Autoplay Engine using content filtering & recency penalty math, and a reliable background Scrobbling Infrastructure supporting Last.fm, ListenBrainz, and Open Scrobbler via WorkManager.

## Key Features & Requirements

### 1. 16-Band Parametric Equalizer & Audio DSP Sheet
- **Native DSP Engine:** Utilizes Android's native `android.media.audiofx.DynamicsProcessing` API attached directly to ExoPlayer's `audioSessionId`.
- **16-Band EQ:** Multi-band parametric EQ stages with customizable gain, center frequencies, and Q-factors.
- **Audio Processing Controls:** Native Peak Limiter (to prevent digital clipping) and Low/High Shelf filters for Bass Boost and Treble adjustment. Handles hardware audio offload switching appropriately when EQ is toggled.
- **Device Fallback:** Try-catch wrapper falling back gracefully to standard 5-band `Equalizer` on unsupported low-end OEM hardware.
- **UI Integration:** Expressive Material 3 Audio & DSP Bottom Sheet with custom frequency sliders, preset selector (Flat, Rock, Jazz, Bass Boost, Custom), and real-time bypass toggle.

### 2. Offline Intelligent Autoplay Algorithm
- **Content Filtering & Recency Penalty Scoring:** Room DB query scoring engine evaluating candidate tracks when the queue reaches < 2 items:
  $$\text{Score}(T) = (w_g \cdot S_{\text{genre}}) + (w_a \cdot S_{\text{artist}}) + (w_b \cdot S_{\text{bpm}}) + (w_m \cdot S_{\text{mood}}) - P_{\text{recency}}$$
- **Seamless Pre-fetching:** Calculations run on `Dispatchers.IO` to ensure $0\text{ms}$ latency transitions between the last queue track and recommended auto-played tracks.
- **Fallback Hierarchy:** High-confidence match ($>0.65$) $\rightarrow$ Genre match ($0.35-0.65$) $\rightarrow$ Top favorited/played non-recent track fallback.
- **UI Control:** Persisted setting & Queue Sheet toggle (`"Autoplay Similar Tracks"`).

### 3. Background Scrobbling Infrastructure (Last.fm / ListenBrainz / Open Scrobbler)
- **Scrobble Threshold Rule:** Tracks are scrobbled ONLY after reaching 50% duration OR 240 seconds (minimum track length: 30 seconds).
- **Offline First WorkManager Architecture:** Scrobble events are written to local Room DB (`ScrobbleQueueEntity`) as `PENDING` and synced in batches using `WorkManager` with network (`NetworkType.CONNECTED`) and battery constraints (`setRequiresBatteryNotLow(true)`) plus exponential backoff retries.
- **Multi-Service Support:** Extensible `ScrobbleProvider` abstraction supporting Last.fm API, ListenBrainz Token Auth, and Open Scrobbler.
- **Settings Control:** Individual service authentication and toggles under Settings $\rightarrow$ `"Scrobbling & Statistics"`.

## Out of Scope
- Direct cloud playback/streaming.
- Online recommendation APIs (all recommendation logic remains 100% offline).
