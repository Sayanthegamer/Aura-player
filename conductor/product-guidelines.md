# Product Guidelines: Aura Player

## Design Philosophy & Aesthetics
- **Material 3 Expressive UI:** Clean, modern, dynamic Android native design utilizing Material 3 components, expressive typography (Inter/Roboto), dynamic color extraction from album artwork, and subtle micro-animations.
- **No Unnecessary Clutter / No Excessive Glassmorphism:** Focus on clarity, performance, legibility, and refined surface container elevation rather than heavy blur effects or decorative glassmorphism.
- **Progressive Disclosure:**
  - Main Screen: Ultra-clean full-screen album art, player controls, track info, lyrics toggle button, and high-res quality chip.
  - Contextual Sheets: Long-press or chip tap expands deep controls (ReplayGain sliders, Audio DSP settings, EQ, track metadata editor).

## Audio & Interactive Principles
- **60/120Hz Smoothness:** All animations (lyric karaoke highlighting, progress bars, sheet transitions) must run at native display refresh rates without UI thread jank.
- **Uncompromising Ergonomics:** Large touch targets, intuitive swiping gestures, smooth cross-fades between album art and lyric view.
- **Informative Badges:** Technical audio metrics (e.g., `FLAC • 24-bit/96kHz`, `RG -2.1dB`) displayed as unobtrusive, tappable badges.
