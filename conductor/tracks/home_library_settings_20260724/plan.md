# Implementation Plan - Phase 2: Home Screen, Library Scanner & Settings

## Phase 2.1: Local Media Library Scanner & Room Persistence Layer
- [ ] Task: Create Room DB entities (`TrackEntity`, `AlbumEntity`, `ArtistEntity`) and DAOs (`TrackDao`).
- [ ] Task: Implement `MediaScanner` querying Android `MediaStore` with coroutine flow progress reporting.
- [ ] Task: Write TDD unit tests for `MediaScanner` parsing and Room DB track operations.
- [ ] Task: Phase Verification & Checkpoint (Refer to workflow.md)

## Phase 2.2: Material 3 Expressive Home Screen & Mini-Player
- [ ] Task: Build Compose `HomeScreen` with TopAppBar, Search, and Tabbed Dashboard (Songs, Albums, Artists).
- [ ] Task: Build `MiniPlayer` persistent bottom bar anchored above navigation bar.
- [ ] Task: Connect track selection from `HomeScreen` to `PlayerManager` playback and mini-player expansion.
- [ ] Task: Phase Verification & Checkpoint (Refer to workflow.md)

## Phase 2.3: Comprehensive Settings Screen
- [ ] Task: Build Compose `SettingsScreen` with theme toggle, ReplayGain adjustment, manual scanner trigger, and About section.
- [ ] Task: Integrate Settings navigation and Preferences DataStore.
- [ ] Task: Phase Verification & Checkpoint (Refer to workflow.md)
