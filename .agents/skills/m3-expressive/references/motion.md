# Material 3 Motion System, Easing & Transition Patterns

Based on official [Material 3 Motion Guidelines](https://m3.material.io/styles/motion/overview/how-it-works).

## 🎬 1. Motion Principles: How It Works

Material 3 Motion operates on three core principles:

1. **Informative**: Guides the user by demonstrating spatial relationships, origins, and hierarchy between components.
2. **Focused**: Directs attention to primary visual focal points without causing distraction.
3. **Expressive**: Adds character, fluidity, and polish using dynamic spring physics and responsive easing curves.

---

## ⏳ 2. Easing Curves & Duration Scale

### Easing Spec Table

| Easing Category | Easing Curve Specification | Usage |
|:---|:---|:---|
| **Emphasized** | `CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)` | Primary container morphing & screen expansions |
| **Emphasized Accelerate** | `CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)` | Elements leaving the screen (Exit) |
| **Emphasized Decelerate** | `CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)` | Elements entering the screen (Enter) |
| **Standard** | `CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)` | Auxiliary controls, switches, and sliders |
| **Standard Accelerate** | `CubicBezierEasing(0.3f, 0.0f, 1.0f, 1.0f)` | Small exit animations |
| **Standard Decelerate** | `CubicBezierEasing(0.0f, 0.0f, 0.0f, 1.0f)` | Small enter animations |

### Duration Scale Specification

```kotlin
object M3MotionDurations {
    const val Short1 = 50   // Icon press scale & micro-feedback
    const val Short2 = 100  // Selection toggles & checkbox ticks
    const val Short3 = 150  // Tooltip & chip appearance
    const val Short4 = 200  // Small popover menus
    const val Medium1 = 250 // Card selection fill
    const val Medium2 = 300 // Standard card expansion / collapse
    const val Medium3 = 350 // Modal bottom sheet entry
    const val Medium4 = 400 // Complex dialog reveal
    const val Long1 = 450   // Full-screen page transitions
    const val Long2 = 500   // Activity transitions
    const val Long3 = 600   // Shared element morphing
    const val Long4 = 700   // Hero container expansion
}
```

---

## 🔀 3. The 4 M3 Transition Patterns

Material 3 defines 4 standard transition patterns for navigation and layout changes:

### Pattern A: Container Transform (Morphing)
Morphs one UI container into another (e.g. MiniPlayer -> Full Screen Player, Album Thumbnail -> Album Detail).

```kotlin
// Compose Shared Element Container Transform Pattern
SharedTransitionLayout {
    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            fadeIn(animationSpec = tween(300, easing = M3Motion.EmphasizedDecelerate)) +
            scaleIn(initialScale = 0.92f, animationSpec = spring(stiffness = Spring.StiffnessLow)) togetherWith
            fadeOut(animationSpec = tween(200, easing = M3Motion.EmphasizedAccelerate))
        }
    ) { screen ->
        // Render screen with sharedBounds or sharedElement modifier
    }
}
```

### Pattern B: Shared Axis (X, Y, Z Axis Motion)
Used for content with spatial or sequential relationships:
* **X-Axis (Horizontal)**: Tab switching, step-by-step onboarding, album pagination.
* **Y-Axis (Vertical)**: List sorting, filter expansions.
* **Z-Axis (Depth)**: Entering a sub-level menu or hierarchy.

```kotlin
// X-Axis Shared Axis Transition Spec (Forward navigation)
val SharedAxisXForward = slideInHorizontally(
    initialOffsetX = { width -> (width * 0.3f).toInt() },
    animationSpec = tween(400, easing = M3Motion.EmphasizedDecelerate)
) + fadeIn(animationSpec = tween(300)) togetherWith
slideOutHorizontally(
    targetOffsetX = { width -> -(width * 0.3f).toInt() },
    animationSpec = tween(300, easing = M3Motion.EmphasizedAccelerate)
) + fadeOut(animationSpec = tween(200))
```

### Pattern C: Fade Through
Used when there is **no spatial relationship** between screens (e.g. switching main navigation sections like Home -> Search -> Settings).

```kotlin
// Fade Through Transition Spec
val FadeThroughSpec = fadeIn(
    animationSpec = tween(durationMillis = 210, delayMillis = 90, easing = M3Motion.EmphasizedDecelerate)
) + scaleIn(
    initialScale = 0.92f,
    animationSpec = tween(durationMillis = 210, delayMillis = 90, easing = M3Motion.EmphasizedDecelerate)
) togetherWith fadeOut(
    animationSpec = tween(durationMillis = 90, easing = M3Motion.EmphasizedAccelerate)
)
```

### Pattern D: Fade / Scale
Used for UI elements that enter or exit within the screen bounds without a container (e.g., Dialogs, Floating Action Buttons, Context Menus).

```kotlin
// Fade / Scale Spec (Dialogs & Popovers)
val FadeScaleEnter = fadeIn(animationSpec = tween(150)) + scaleIn(
    initialScale = 0.8f,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
)
val FadeScaleExit = fadeOut(animationSpec = tween(100)) + scaleOut(targetScale = 0.8f)
```
