# Implementation Plan: Material 3 Expressive UI/UX Redesign

## Phase 1: Vertical Equalizer Band UI Refactoring & Presets
- [x] Task: Refactor `AudioDspBottomSheet.kt` to replace horizontal sliders with vertical EQ band gain sliders (`VerticalEqBandSlider` with $\pm 12\text{dB}$ range)
- [x] Task: Integrate `SingleChoiceSegmentedButtonRow` for EQ presets (Flat, Rock, Jazz, Bass Boost, Custom)
- [x] Task: Phase Verification & Checkpoint (Refer to workflow.md)


## Phase 2: Now Playing Hero Carousel, Gaussian Canvas & Hero 96dp FAB
- [x] Task: Refactor Now Playing artwork container to use M3 Multi-Browse Hero Carousel (`HorizontalPager` offset scaling)
- [x] Task: Implement `ScopedPlayerTheme` and Gaussian Brownian Motion background canvas for album artwork dynamic color transitions
- [x] Task: Implement $96\text{dp} \times 96\text{dp}$ Hero `LargeFloatingActionButton` for central Play/Pause control with $48\text{dp}$ media icon buttons
- [x] Task: Phase Verification & Checkpoint (Refer to workflow.md)


## Phase 3: Adaptive Navigation Shell & Global M3 Token Audit
- [x] Task: Implement `AuraAdaptiveNavigation` (Bottom `NavigationBar` for portrait, `NavigationRail` for landscape/tablets)
- [x] Task: Audit and update all list items (`72dp` 2-line `ListItem`), chips (`FilterChip`), dialogs (`AlertDialog`), and sleep timer pickers (`TimePicker`)
- [x] Task: Phase Verification & Checkpoint (Refer to workflow.md)

