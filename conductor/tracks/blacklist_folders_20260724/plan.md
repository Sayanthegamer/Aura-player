# Implementation Plan - Blacklist Folder Feature

## Phase 1: DataStore & MediaScanner Filtering Logic
- [ ] Task: Update `SettingsPreferences` to support `blacklistedFolders: Set<String>` in DataStore preferences.
- [ ] Task: Update `MediaScanner` to retrieve blacklisted folders and filter out matching file paths during query.
- [ ] Task: Write TDD unit tests for path filtering logic in `MediaScannerTest`.
- [ ] Task: Phase Verification & Checkpoint (Refer to workflow.md)

## Phase 2: Settings UI Exclusion Manager & Auto-Rescan Integration
- [ ] Task: Build Compose `BlacklistFolderManager` card and dialog in `SettingsScreen`.
- [ ] Task: Wire folder additions/deletions from `SettingsScreen` to `MusicRepository` and trigger auto-rescan.
- [ ] Task: Verify end-to-end functionality via unit tests and debug build.
- [ ] Task: Phase Verification & Checkpoint (Refer to workflow.md)
