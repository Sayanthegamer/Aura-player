# Track Specification: Phase 2 - Home Screen, Library Scanner & Settings

## Overview
Implement the core music library experience for Aura Player: an automatic background MediaStore local audio scanner, Room DB persistence layer for indexed songs, albums, and artists, a modern Material 3 Expressive Home Screen Dashboard with tabs and persistent mini-player, and a comprehensive Settings screen.

## Functional Requirements
1. **Local Audio Library Scanner & Persistence:**
   - Query `MediaStore.Audio.Media.EXTERNAL_CONTENT_URI` to scan local files (`.mp3`, `.flac`, `.m4a`, `.wav`, `.aac`).
   - Store scanned audio tracks, albums, and artists in Room Database (`TrackEntity`, `AlbumEntity`, `ArtistEntity`).
   - Handle runtime permission requests (`READ_MEDIA_AUDIO` / `READ_EXTERNAL_STORAGE`).
   - Expose real-time scanning progress StateFlow to UI.

2. **Material 3 Expressive Home Screen:**
   - Top App Bar featuring Aura Player branding, global library search button, and Settings gear icon.
   - Quick-Play Hero Card displaying recently played track with 1-tap play action.
   - Expressive Scrollable Material 3 Tabs: **Songs**, **Albums**, **Artists**, **Playlists**, **Recent**.
   - Interactive track list items with artwork thumbnails, title, artist, duration, and overflow menu (Play Next, Add to Playlist).
   - Persistent Bottom Mini-Player Bar displaying current track title, artwork thumbnail, play/pause toggle, and tap gesture to expand into full `PlayerScreen`.

3. **Comprehensive Settings Menu:**
   - **Appearance & Theme:** Dynamic Wallpaper (Monet), Dark Mode, Light Mode options.
   - **Audio Engine & ReplayGain:** ReplayGain target loudness adjustment (-18 LUFS / -14 LUFS) and peak anti-clipping toggle.
   - **Library Management:** Manual "Rescan Music Library" trigger with progress bar and folder inclusion/exclusion manager.
   - **About & Build Info:** Version, license information, and system environment info.

## Acceptance Criteria
- App successfully requests media permissions and scans local device storage for audio files.
- Scanned tracks populate the Home Screen tabs (Songs, Albums, Artists) smoothly without UI thread blocking.
- Tapping any track in the Home Screen updates `PlayerManager` and starts playback, updating the persistent mini-player.
- Tapping the mini-player smoothly expands into the full Material 3 Expressive `PlayerScreen`.
- Settings menu items correctly update app preferences and trigger manual library rescans.
