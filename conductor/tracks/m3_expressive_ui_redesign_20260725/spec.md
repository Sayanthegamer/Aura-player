# Track Specification: Complete Material 3 Expressive UI/UX Redesign

## Overview
This track executes a comprehensive UI/UX overhaul of Aura Player to strictly comply with official Google Material 3 Expressive design specifications detailed in `.agents/skills/m3-expressive/`.

## Functional Requirements

### 1. 16-Band Vertical Equalizer Band UI (`AudioDspBottomSheet.kt`)
- Replace horizontal sliders with proper vertical EQ band sliders (`VerticalEqBandSlider` with $\pm 12\text{dB}$ gain range).
- Incorporate Material 3 Centered Slider zero-notch styling.
- Integrate `SingleChoiceSegmentedButtonRow` for EQ presets (Flat, Rock, Jazz, Bass Boost, Custom).

### 2. Now Playing Screen & Dynamic Canvas
- Implement M3 Multi-Browse Carousel ($70–80\%$ focal width) with corner radius morphing ($16\text{dp} \to 28\text{dp}$).
- Implement Gaussian Brownian Motion animated background canvas.
- Use `ScopedPlayerTheme` for artwork content-based dynamic color scheme transitions.
- Implement Hero $96\text{dp} \times 96\text{dp}$ `LargeFloatingActionButton` for central Play/Pause control paired with $48\text{dp}$ media control icon buttons.

### 3. Adaptive Application Navigation Shell
- Implement `AuraAdaptiveNavigation` supporting:
  - Mobile Portrait: Bottom `NavigationBar` ($80\text{dp}$ height, Level 2 $3\text{dp}$ elevation, $64\text{dp} \times 32\text{dp}$ active indicator pill).
  - Mobile Landscape / Tablet: Side `NavigationRail` ($80\text{dp}$ width).

### 4. Component & Token Audit
- Enforce $48\text{dp} \times 48\text{dp}$ touch target boxes across all icon buttons and clickable controls.
- Standardize track list items to $72\text{dp}$ 2-line `ListItem`.
- Update genre selectors to `FilterChip` with $18\text{dp}$ checkmark icons.
- Update sleep timer picker to M3 `TimePicker` inside $28\text{dp}$ dialog.

## Non-Functional Requirements
- Maintain 60/120 FPS Compose rendering performance.
- Preserve ExoPlayer hardware offload when EQ is disabled.
- Full TalkBack accessibility announcements and merged semantic targets.

## Acceptance Criteria
- All UI components pass Material 3 Expressive design token verification.
- `./gradlew assembleDebug` compiles cleanly without errors.
