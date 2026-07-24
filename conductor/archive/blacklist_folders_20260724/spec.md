# Track Specification: Blacklist Folder Feature

## Overview
Implement folder exclusion/blacklisting capability for Aura Player. Users can specify directories (e.g. WhatsApp audio, Telegram media, voice notes, call recordings) that `MediaScanner` should ignore during media indexing.

## Functional Requirements
1. **Blacklist Storage & Preferences:**
   - Store set of blacklisted folder paths in DataStore (`SettingsPreferences.blacklistedFoldersFlow`).
   - Expose methods `addBlacklistedFolder(path: String)` and `removeBlacklistedFolder(path: String)`.

2. **MediaScanner Filtering:**
   - Modify `MediaScanner.scanLibrary()` to fetch or accept blacklisted folder paths.
   - Filter out any audio file whose `filePath` starts with or matches any path in the blacklisted folders set.
   - Purge blacklisted tracks from Room DB on rescan.

3. **Settings UI Folder Exclusion Manager:**
   - Add "Excluded Folders (Blacklist)" section in `SettingsScreen`.
   - Display active blacklisted folder paths with a 1-tap delete button.
   - Provide "Add Excluded Path" dialog with quick-add preset buttons (e.g. `WhatsApp`, `Telegram`, `Recordings`).
   - Trigger library rescan automatically when blacklist changes.

## Acceptance Criteria
- Files inside blacklisted directories are completely filtered out during library scanning and do not appear in Songs, Albums, or Artists tabs.
- Removing a folder from the blacklist and triggering rescan reinstates its songs to the library.
- Blacklist preferences persist across app restarts.
- Unit tests verify `MediaScanner` path filtering logic.
