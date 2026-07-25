# Track Specification: Personalized Home & Library Manager (v5)

## Overview
Replace the current generic Home screen (static alphabetical hero card + 3 flat tabs) with a personalized, data-driven dashboard, and elevate library management out of Settings into its own dedicated section. This track also closes a data gap that silently defeats personalization: `TrackEntity.bpm/genre/moodTags` are never populated by `MediaScanner`, so `AutoplayScorer` and any new "smart" Home rails are currently scoring against empty defaults.

## Prerequisite: Fix the Metadata Gap & Technical Architecture
- **2-Pass Scan Architecture:** `MediaScanner.scanLibrary()` performs a fast MediaStore query pass first to populate the UI instantly, followed by an asynchronous background enrichment pass using `jaudiotagger` to extract embedded `GENRE` / `TCON` tags and Artwork presence (`hasArtwork`) without freezing library scanning.
- **2-Tier Artwork Presence Probe:** `hasArtwork` is evaluated via a 2-tier check: Tier 1 (`MetadataTagParser` embedded artwork check) + Tier 2 (`ContentResolver.openInputStream(Uri.parse(albumArtUri))` stream check). If either succeeds, `hasArtwork = true`, preventing false positive alerts for files with `folder.jpg` or MediaStore art cache.
- **`ScanState.Enriching` & Job Safety:** Extend `ScanState` with `Enriching(current, total)` state for UI progress signaling, cancelling any active `enrichmentJob` before starting a new rescan.
- **Room Migration 4 → 5 & Automated Test:** Upgrade `AuraDatabase` with `MIGRATION_4_5` (`ALTER TABLE tracks ADD COLUMN hasArtwork INTEGER NOT NULL DEFAULT 1`) and verify via `RoomMigrationTest.kt` in `src/androidTest/` to preserve existing database records and scrobble queue items.
- **Domain Extraction & 100% Pure JVM Tests:** Extract pure business logic into standalone Kotlin objects (`HomeRailBuilder`, `DuplicateDetector`, `LibraryHealthCalculator`, `MetadataTagParser`, `TagUpdateCalculator`) enabling fast unit tests in `src/test/`. `SettingsPreferences` receives `DataStore<Preferences>` via primary constructor.
- **Hand-Rolled Carousel Pattern:** Implement the "Made For You" carousel using the hand-rolled `LazyRow`/`HorizontalPager` pattern from `components.md` to avoid experimental M3 BOM API mismatches.

## Functional Requirements

### 1. Personalized Home Redesign (`HomeScreen.kt`)
- **Dynamic Greeting Header:** Time-of-day greeting ("Good morning", "Good afternoon", "Good evening").
- **Continue Listening:** Hero card driven by `lastPlayedTimestamp` (within last 7 days), falling back to "Recently Added" hero on fresh install.
- **Made For You:** Horizontal carousel seeded from the most recently played track, using `AutoplayScorer.calculateScore` against full library, deduped by artist via `HomeRailBuilder`.
- **Most Played Artists & On Repeat:** Sourced from scrobble history.
- **Recently Added:** Sourced from `dateAdded` (last 14 days) with top-20 fallback.
- **User-Configurable Layout:** Persist rail visibility and ordering in `SettingsPreferences`.

### 2. Library Manager (`LibraryManagerScreen.kt`)
- Dedicated top-level destination separate from `SettingsScreen.kt`.
- **Metadata Health Dashboard:** Track counts for missing genre, missing artwork (`hasArtwork = false`), zero BPM computed via `LibraryHealthCalculator`.
- **Tag Editor:** Bulk edit `genre` and `moodTags` for multi-selected tracks or albums via `TagUpdateCalculator`.
- **Duplicate Detection:** Render duplicate track groups via `DuplicateDetector`.
- **Batch Operations & Blacklist Folder Manager:** Moved out of Settings.

## Acceptance Criteria
- Room DB safely migrates from v4 to v5 without wiping scrobble history.
- 2-pass media scan updates progress via `ScanState.Enriching`.
- All 5 pure logic unit test files pass cleanly in `src/test/`.
- `./gradlew test` passes and `./gradlew assembleDebug` compiles cleanly.
