---
name: rhythm-ui-reference
description: Visual/UX reference brief for the "Rhythm" music player (github.com/cromaguy/Rhythm). Use this whenever a spec or prompt says "Rhythm-inspired," "like Rhythm but cleaner," or references morphism/animation quality from that app. This file exists so an agent never has to guess which parts of Rhythm are the inspiration and which parts are the warning label.
---

# Reference Brief: Rhythm Player — What to Steal, What to Reject

## Why this file exists
"Make it look like Rhythm" is not a spec — Rhythm is simultaneously the best-looking
and the most poorly-organized player in this genre. Without this doc, an agent will
either copy its clutter along with its charm, or over-correct into something generic
and lose the thing that actually made it good. This brief separates the two so
Antigravity (or any agent) can pursue the aesthetic without inheriting the flaws.

**Source of truth for tokens/specs:** `.agents/skills/m3-expressive/` still governs
every concrete value (corner radii, elevation, spacing, motion durations). This
document only governs *judgment calls* — density, hierarchy, disclosure — that the
token tables can't decide on their own.

---

## ✅ Part 1 — What Rhythm does that we WANT (10/10 creativity, animation, morphism)

Reference these traits by name in specs; don't just say "Rhythm-like":

1. **Consistent shape morphing across state changes.** Corner radii, container
   sizes, and elevation shift smoothly during transitions (mini-player → full
   player, collapsed → expanded sheet) rather than snapping. This is the single
   biggest contributor to the "feels good to use" reaction — motion continuity,
   not motion quantity.
2. **Bold, committed dynamic theming.** Rhythm leans hard into Material You /
   album-art-driven color rather than a timid tint. When we say "more expressive,"
   we mean *confidently* shifting surface + accent colors, not just tinting one
   badge.
3. **Morphism used as feedback, not decoration.** Blur/glass surfaces appear
   specifically on interactive overlays (sheets, now-playing) to signal layering,
   not on every card. Emulate the *purpose* (depth cue), not the *quantity*.
4. **A genuinely rich feature set presented behind Progressive Disclosure** —
   10-band EQ, replay gain, stats, streaming modes, all exist but aren't all on
   screen at once. This is the part our own `product.md` philosophy already
   asks for — Rhythm proves that "audiophile-deep + clean surface" is achievable,
   it just doesn't consistently deliver the "clean surface" half.

**Action for Antigravity:** when a task references "the good parts of Rhythm,"
it means items 1–4 above — motion continuity, confident (not muted) dynamic color,
purposeful glass/blur, and a genuinely deep feature set. It does **not** mean
copying screen density or button placement.

---

## ❌ Part 2 — What Rhythm does that we explicitly REJECT

| Rhythm trait | Why it's rejected here | Concrete rule for this project |
|---|---|---|
| **Visual clutter / "go random" layouts** | Screens don't follow a repeatable grid or hierarchy — every screen invents its own layout logic, so nothing feels learnable. | Every screen must map to one of the M3 layout patterns already defined in `components.md` (ListItem, Card variants, Sheets, Nav). No bespoke one-off layouts per screen. |
| **Broken/inconsistent responsiveness** | Tablet vs phone layouts diverge ad hoc rather than adapting from one system. | Use a single `AuraAdaptiveNavigation`-style breakpoint system (already in our plan) — one source of truth for width-class behavior, not per-screen tablet hacks. |
| **Accessibility as an afterthought (3/10)** | Low-contrast overlays under morphism, tiny/ambiguous touch targets, decorative-only state cues. | Every interactive element keeps the 48dp touch target and 4.5:1 text contrast rules from `foundations.md` even inside blurred/glass surfaces — no exceptions for "vibe" screens. |
| **Lyrics tab: controls dominate content (5/10)** | Buttons/toolbar eat the vertical space that should be lyrics. | See Part 3 — hard rule for our `LyricCanvas`. |
| **Feature-count taking priority over hierarchy** | Because everything is visible/reachable at once, no single screen has a clear primary action. | Every screen gets exactly one primary action at top visual weight; everything else goes into a sheet, menu, or secondary chip — Progressive Disclosure is not optional polish, it's the acceptance criterion. |

---

## 🎤 Part 3 — Specific fix: Lyrics screen control-to-content ratio

Rhythm's lyrics view was scored 5/10 specifically because chrome (buttons/toolbar)
competes with the lyrics for space. For our `LyricCanvas.kt`, the rule is:

- **Default state:** only 2 icon buttons visible (Tune/offset, Close) — both
  already 48dp touch targets, both already collapse-by-default. This is correct;
  don't add more persistent buttons to this screen.
- **Any new lyrics feature (translation toggle, romanization toggle, font-size,
  etc.) must go inside the existing collapsible offset-sheet pattern**, not
  become a new always-visible icon in the header row. If the header row would
  ever need a 3rd+ icon, that's the signal to move it into an overflow menu
  instead.
- **Content region (the lyric lines themselves) must always claim ≥80% of
  vertical space** when no sheet is expanded. Treat this as a testable
  acceptance criterion, not a guideline.

---

## How to invoke this in a spec

When writing a future `spec.md`, reference it explicitly, e.g.:

> "Style the queue sheet per `rhythm-ui-reference.md` Part 1 (items 1–2: shape
> morphing + confident dynamic color) — do NOT replicate Rhythm's information
> density; follow Part 2's single-primary-action rule instead."

This gives Antigravity a named, checkable target instead of an adjective.
