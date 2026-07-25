# Track Implementation Plan: Personalized Home & Library Manager (v5)

## Phase 1: Room Migration, Data Layer & Asynchronous Scanner
- [x] Task: Add `hasArtwork: Boolean` to `TrackEntity` and implement `MIGRATION_4_5` in `AuraDatabase.kt` (bump version to 5).
- [x] Task: Create `RoomMigrationTest.kt` in `src/androidTest/` to test `MIGRATION_4_5` database migration.
- [x] Task: Create standalone pure logic objects `MetadataTagParser.kt`, `DuplicateDetector.kt`, and `LibraryHealthCalculator.kt`.
- [x] Task: Implement 2-pass scan in `MediaScanner.kt` with 2-tier artwork presence probe (Tier 1 `MetadataTagParser` + Tier 2 `ContentResolver.openInputStream`), `ScanState.Enriching`, and job cancellation safety.
- [x] Task: Update `TrackDao.kt` with tag editing, health queries (`hasArtwork = 0`), and flexible sorting.
- [x] Task: Refactor `SettingsPreferences.kt` constructor to accept `DataStore<Preferences>` and add rail/view settings.
- [x] Task: Write pure JVM unit test `MediaScannerMetadataTest.kt` for `MetadataTagParser`.
- [x] Task: Write pure JVM unit test `SettingsPreferencesTest.kt` using temp `DataStore`.
- [x] Task: Phase 1 Verification & Checkpoint.

## Phase 2: Domain Logic, Personalized Home & HomeRailBuilder
- [x] Task: Create `HomeRailBuilder.kt` to handle artist deduplication, recency fallbacks, and user rail ordering.
- [x] Task: Implement dynamic greeting header in `HomeScreen.kt`.
- [x] Task: Implement "Continue Listening" hero card using `lastPlayedTimestamp`.
- [x] Task: Implement "Made For You" horizontal carousel using hand-rolled `LazyRow`/`HorizontalPager` pattern from `components.md` and `AutoplayScorer`.
- [x] Task: Implement "Most Played Artists", "Recently Added", and "On Repeat" rails.
- [x] Task: Add Home Rail customization sheet using `SettingsPreferences`.
- [x] Task: Write pure JVM unit test `HomeRailDeduplicationTest.kt` for `HomeRailBuilder`.
- [x] Task: Phase 2 Verification & Checkpoint.

## Phase 3: Library Manager, Health Dashboard & Tag Editor
- [x] Task: Create `TagUpdateCalculator.kt` for pure bulk tag edit calculations.
- [x] Task: Build `LibraryManagerScreen.kt` with Metadata Health Dashboard cards.
- [x] Task: Implement Tag Editor bottom sheet / dialog for bulk tag updates.
- [x] Task: Implement Duplicate Detection resolution view using `DuplicateDetector`.
- [x] Task: Move Blacklist Folder Manager into `LibraryManagerScreen.kt`.
- [x] Task: Connect `LibraryManagerScreen` navigation in `MainActivity.kt`.
- [x] Task: Write pure JVM unit test `TagEditorTest.kt` for `TagUpdateCalculator`.
- [x] Task: Write pure JVM unit test `DuplicateDetectionTest.kt` for `DuplicateDetector`.
- [x] Task: Phase 3 Verification & Checkpoint.

## Phase 4: Final Integration & Verification
- [x] Task: Run `./gradlew test` and verify all unit tests pass.
- [x] Task: Run `./gradlew assembleDebug` and verify clean build.
- [x] Task: Phase 4 Verification & Checkpoint.

