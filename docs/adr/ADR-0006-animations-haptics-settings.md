# ADR-0006: Micro-Animations, Haptics Abstraction & Global Settings

> **Status:** Accepted  
> **Date:** 2026-02-17  
> **Category:** UX polish, platform abstraction, settings persistence

---

## Context

The app currently has no transition animations, no haptic feedback, and no user-configurable settings.
Phase 4 adds:
1. Subtle, iOS-smooth micro-animations to screen transitions, answer selection, progress bar, and result display.
2. Haptic feedback on key user interactions (answer tap, quiz result), gated by a global toggle.
3. A global settings system with persistence (haptics toggle + sound toggle placeholder).

All features must work cross-platform (Android + iOS) with minimal dependencies and no leaking of platform APIs into domain or presentation layers.

---

## Decisions

### D1: Settings Persistence — `multiplatform-settings` (russhwolf)

**Options considered:**

| Option | Pros | Cons |
|---|---|---|
| `com.russhwolf:multiplatform-settings` 1.3.0 | Mature, actively maintained, KMP-native, wraps `SharedPreferences` (Android) + `NSUserDefaults` (iOS). Coroutines extension available. Tiny footprint. | New dependency. |
| Manual `expect/actual` with `DataStore` / `NSUserDefaults` | No new deps. | Boilerplate, DataStore requires extra KMP setup, error-prone. |
| SQLDelight (reuse existing) | Already in project. | Overkill for key-value settings; schema changes for non-question data. |

**Decision:** Use `multiplatform-settings` 1.3.0 with the coroutines extension.
- Dependency: `com.russhwolf:multiplatform-settings:1.3.0` (commonMain)
- Coroutines: `com.russhwolf:multiplatform-settings-coroutines:1.3.0` (commonMain)
- Android factory: `SharedPreferencesSettings.Factory` (needs `Context`)
- iOS factory: `NSUserDefaultsSettings.Factory` (no special setup)
- Platform factory created via `expect/actual` `SettingsFactory` in `shared/data/settings/`.

**Rationale:** Least boilerplate, battle-tested in KMP ecosystem, no schema management, trivially testable with `MapSettings` in-memory backend.

### D2: Haptics Abstraction — `expect/actual` in `composeApp`

**Decision:** Place haptics abstraction in `composeApp` (not `shared`), because:
- Haptics is a UI/platform concern, not domain logic.
- The haptics interface is consumed only by UI-layer effect collectors.
- `shared/domain` must stay pure (no platform APIs).
- `shared/data` doesn't need haptics.

**Structure:**
- `composeApp/src/commonMain/.../platform/Haptics.kt` — interface + `ImpactType` enum
- `composeApp/src/androidMain/.../platform/AndroidHaptics.kt` — `Vibrator` / `VibrationEffect`
- `composeApp/src/iosMain/.../platform/IosHaptics.kt` — `UIImpactFeedbackGenerator` / `UINotificationFeedbackGenerator`
- Provided via `CompositionLocal` (`LocalHaptics`) for consumption in composables.

**Haptics is NOT called from ViewModel.** Instead:
- ViewModel emits `Effect.PlayHaptic(type)`.
- UI effect collector checks `settingsState.hapticsEnabled` and invokes `Haptics.impact(type)`.
- This keeps VM platform-free and settings-aware without injecting settings into haptics.

### D3: Settings Architecture — Domain interfaces + data implementation

**Layers:**
- **Domain:** `SettingsRepository` interface (in `shared/domain/repository/`), use cases
- **Data:** `SettingsRepositoryImpl` (in `shared/data/settings/`) using `multiplatform-settings`
- **Presentation:** No dedicated SettingsViewModel for Phase 4 (no settings screen yet). Settings state is provided via `CompositionLocal` from `AppNavigation`.

**Model:** `SettingsState(hapticsEnabled: Boolean, soundEnabled: Boolean)` — in `shared/domain/model/`.

### D4: Animation Approach — Standard Compose APIs only

**No new animation library.** Use:
- `AnimatedContent` + `ContentTransform` (custom `transitionSpec`) for screen transitions
- `animateColorAsState` for `AnswerOption` color transitions
- `animateFloatAsState` for `QuizProgressBar`
- `AnimatedVisibility` with `fadeIn + slideInVertically` for `ResultCard` entry
- `Modifier.graphicsLayer { scaleX; scaleY }` + `animateFloatAsState` for press feedback
- `tween` easing with 300ms default duration; `EaseInOutCubic` curve

**No spring/bounce.** Subtle, iOS-like: short duration, gentle easing, small displacement (8-16dp).

### D5: MVI Effect for Haptics

**QuizEffect additions:**
```kotlin
data class PlayHaptic(val type: ImpactType) : QuizEffect
```

**ResultEffect additions:**
```kotlin
data class PlayHaptic(val type: ImpactType) : ResultEffect
```

ViewModel emits `PlayHaptic(Light)` on answer selection, `PlayHaptic(Success)` on high score, `PlayHaptic(Error)` on low score. UI collector handles the call.

### D6: Sound Toggle — Plumbing Only

- `soundEnabled: Boolean` in `SettingsState` + persisted via `multiplatform-settings`.
- `SoundPlayer` interface + `SoundEffect` enum defined (no implementation).
- Actual sound playback deferred to Phase 5+.

---

## Consequences

### Positive
- Settings system is reusable for future preferences (theme, language, difficulty).
- Haptics abstraction is clean and testable (can be faked in tests).
- Animations use only standard Compose APIs — no version-lock to third-party libs.
- Sound toggle is pre-wired; adding audio later requires only `actual` implementations.

### Negative
- New dependency: `multiplatform-settings` (1 lib, ~50KB).
- `expect/actual` surface grows (haptics + settings factory).
- No settings UI screen yet — toggles accessible only programmatically or via debug section.

### Neutral
- Module boundary unchanged (no new Gradle modules).
- Domain layer gains `SettingsRepository` interface + 2 use cases — minimal expansion.

---

## Follow-ups

- [ ] Phase 5: Implement `SoundPlayer` actual implementations.
- [ ] Phase 5+: Add a Settings screen UI (currently deferred — no Figma design).
- [ ] Consider adding a `reduce motion` accessibility toggle (respects system `prefers-reduced-motion`).
- [ ] Consider persisting quiz preferences (difficulty, category) in the same settings system.
