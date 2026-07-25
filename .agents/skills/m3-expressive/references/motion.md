# Material 3 Motion System, Easing & Transition Patterns

Based on official [Material 3 Motion Specs](https://m3.material.io/styles/motion/overview/specs), [Tokens Specs](https://m3.material.io/styles/motion/easing-and-duration/tokens-specs), and [Applying Transitions](https://m3.material.io/styles/motion/transitions/applying-transitions).

## 🎬 1. Motion Principles & Physics Parameters

Material 3 Motion operates on three core principles: **Informative**, **Focused**, and **Expressive**.

### Spring Physics Specifications

| Animation Type | Target Properties | Stiffness Spec | Damping Ratio Spec |
|:---|:---|:---|:---|
| **Spatial / Bounds** | Position, Width, Height | `Spring.StiffnessLow` (300) | `Spring.DampingRatioNoBouncy` (1.0) |
| **Tactile / Press** | Scale, Press Feedback | `Spring.StiffnessMedium` (1500) | `Spring.DampingRatioMediumBouncy` (0.65) |
| **Fade / Opacity** | Alpha, Visibility | `Spring.StiffnessHigh` (10000) | `Spring.DampingRatioNoBouncy` (1.0) |

---

## ⏳ 2. Complete M3 Easing & Duration Tokens

### Cubic Bezier Easing Tokens

```kotlin
object M3Easing {
    // Emphasized Easing (Primary layout morphs & screen expansions)
    val Emphasized = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val EmphasizedAccelerate = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
    val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)

    // Standard Easing (Auxiliary controls & micro-interactions)
    val Standard = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val StandardAccelerate = CubicBezierEasing(0.3f, 0.0f, 1.0f, 1.0f)
    val StandardDecelerate = CubicBezierEasing(0.0f, 0.0f, 0.0f, 1.0f)

    // Legacy Easing
    val LegacyStandard = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
}
```

### Complete Duration Scale Token Table

| Token Name | Duration | Primary Usage |
|:---|:---|:---|
| `short1` | **50 ms** | Micro press scale feedback, icon ripple start |
| `short2` | **100 ms** | Checkbox ticks, active indicator dot reveal |
| `short3` | **150 ms** | Tooltip entry, popover menu fade |
| `short4` | **200 ms** | Chip selection fill, small dropdown open |
| `medium1` | **250 ms** | Card hover elevation shift |
| `medium2` | **300 ms** | Standard card expand / collapse |
| `medium3` | **350 ms** | Modal bottom sheet entry |
| `medium4` | **400 ms** | Complex dialog reveal |
| `long1` | **450 ms** | Navigation rail slide entry |
| `long2` | **500 ms** | Full-screen page transition |
| `long3` | **550 ms** | Shared element container morph |
| `long4` | **600 ms** | Hero container expansion |
| `extraLong1` | **700 ms** | Detailed sheet expansion |
| `extraLong2` | **800 ms** | Ambient color transition |
| `extraLong3` | **900 ms** | Liquid gradient morph |
| `extraLong4` | **1000 ms** | Background Brownian motion cycle |

---

## 🔀 3. Applying Transitions: Timing Ratios & Rules

When animating container morphs and screen transitions, follow these official timing rules:

### A. The 35% / 65% Opacity Split Rule
To prevent muddy overlapping content during container transform transitions:
* **Outgoing Content**: Fades out completely during the **first 35%** of the transition duration.
* **Incoming Content**: Fades in starting at 35% and completes over the **remaining 65%** of the duration.

```kotlin
// 35% / 65% Fade Split Spec in Compose
val ContainerTransformFadeSpec = ContentTransform(
    targetContentEnter = fadeIn(
        animationSpec = tween(
            durationMillis = (600 * 0.65).toInt(), // 390ms
            delayMillis = (600 * 0.35).toInt(),    // 210ms
            easing = M3Easing.EmphasizedDecelerate
        )
    ),
    initialContentExit = fadeOut(
        animationSpec = tween(
            durationMillis = (600 * 0.35).toInt(), // 210ms
            easing = M3Easing.EmphasizedAccelerate
        )
    )
)
```

### B. Shared Bounds Overlay Clipping
Always clip shared bounds to the target container shape during transition overlays to avoid visual bleed:

```kotlin
Modifier.sharedBounds(
    sharedContentState = rememberSharedContentState(key = "player_bounds"),
    animatedVisibilityScope = animatedVisibilityScope,
    clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(16.dp))
)
```

---

## 🔀 4. The 4 M3 Transition Patterns

### Pattern 1: Container Transform (Morphing)
Morphs one UI container into another (e.g. MiniPlayer -> Full Screen Player).

### Pattern 2: Shared Axis (X, Y, Z Axis Motion)
* **X-Axis (Horizontal)**: Tab switching, album pagination.
* **Y-Axis (Vertical)**: List sorting, filter expansions.
* **Z-Axis (Depth)**: Sub-level menu entries.

### Pattern 3: Fade Through
Used when switching non-spatial navigation destinations (e.g., Home -> Search -> Settings).

### Pattern 4: Fade / Scale
Used for dialogs, popovers, and FAB expansions.
