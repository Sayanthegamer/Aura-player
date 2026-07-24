# Implementation Plan - Blacklist Folder Feature

## Phase 1: DataStore & MediaScanner Filtering Logic
- [x] Task: Update `SettingsPreferences` to support `blacklistedFolders: Set<String>` in DataStore preferences.
- [x] Task: Update `MediaScanner` to retrieve blacklisted folders and filter out matching file paths during query.
- [x] Task: Write TDD unit tests for path filtering logic in `MediaScannerTest`.
- [x] Task: Phase Verification & Checkpoint (Refer to workflow.md)

## Phase 2: Settings UI Exclusion Manager & Auto-Rescan Integration
- [x] Task: Build Compose `BlacklistFolderManager` card and dialog in `SettingsScreen`.
- [x] Task: Wire folder additions/deletions from `SettingsScreen` to `MusicRepository` and trigger auto-rescan.
- [x] Task: Verify end-to-end functionality via unit tests and debug build.
- [x] Task: Phase Verification & Checkpoint (Refer to workflow.md)
