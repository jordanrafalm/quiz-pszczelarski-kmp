# ADR-0002: Custom Design System & Theme (Figma-Derived)

> **Date:** 2026-02-17  
> **Status:** Accepted  
> **Supersedes:** None  
> **Related:** ADR-0001 (Compose Multiplatform shared UI)

---

## Context

The UI_figma reference folder defines a complete visual language for Quiz Pszczelarski:
a warm, honey-themed palette, Inter font family, 8dp spacing grid, and rounded card-heavy layout.
Material 3's default theme does not match this design — we need a custom theme layer.

A decision is needed on how to integrate these design tokens into Compose Multiplatform
without hardcoding values across screens.

---

## Options Considered

### Option A: Wrap MaterialTheme with custom color/type overrides only

Override `MaterialTheme(colorScheme = ..., typography = ..., shapes = ...)` at the root.
Access tokens via `MaterialTheme.colorScheme.primary`, etc.

**Pros:** Minimal code, Material 3 components work out of the box.  
**Cons:** Spacing is not part of MaterialTheme; requires a parallel `AppSpacing` object.
Some semantic colors (e.g. `correctAnswer`, `wrongAnswer`) don't map to Material roles.

### Option B: Fully custom theme system (CompositionLocal-based)

Define `AppColors`, `AppTypography`, `AppShapes`, `AppSpacing` as custom classes,
provided via `CompositionLocal`. Material 3 not used at all.

**Pros:** Full control, no Material dependency.  
**Cons:** Lose all Material 3 components (buttons, progress bars, etc.).
Massive reinvention for MVP.

### Option C: MaterialTheme override + App-specific extensions (Hybrid) ← **Selected**

Override `MaterialTheme` colorScheme/typography/shapes with Figma values.
Add `AppSpacing` and `AppColors` extensions (via `CompositionLocal`) for tokens that
don't map to Material roles (spacing grid, quiz-specific semantic colors).

**Pros:** Material 3 components work with custom theme. Quiz-specific tokens available.
Spacing system accessible everywhere.  
**Cons:** Two access paths (`MaterialTheme.colorScheme` for standard roles,
`AppTheme.spacing` / `AppTheme.extendedColors` for extras). Acceptable trade-off.

---

## Decision

**Option C: Hybrid MaterialTheme + App extensions.**

### Theme files to create (in `composeApp/src/commonMain/.../ui/theme/`):

| File | Responsibility |
|---|---|
| `AppColors.kt` | Custom `ColorScheme` factory + extended semantic colors |
| `AppTypography.kt` | Custom `Typography` using Inter font metrics |
| `AppShapes.kt` | Custom `Shapes` (rounded corner radii from Figma) |
| `AppSpacing.kt` | 8dp grid spacing system via `CompositionLocal` |
| `AppTheme.kt` | Root composable wrapping `MaterialTheme` + providing extensions |

### Color mapping (Figma → Material 3 roles):

| Figma Token | Hex | Material 3 Role |
|---|---|---|
| background | `#FFF9ED` | `colorScheme.background` |
| foreground | `#2C2C2C` | `colorScheme.onBackground` / `onSurface` |
| card | `#FFFFFF` | `colorScheme.surface` / `surfaceContainer` |
| card-foreground | `#2C2C2C` | `colorScheme.onSurface` |
| primary | `#FFC933` | `colorScheme.primary` |
| primary-foreground | `#2C2C2C` | `colorScheme.onPrimary` |
| secondary | `#8BA888` | `colorScheme.secondary` |
| secondary-foreground | `#FFFFFF` | `colorScheme.onSecondary` |
| accent | `#A67C52` | `colorScheme.tertiary` |
| accent-foreground | `#FFFFFF` | `colorScheme.onTertiary` |
| muted | `#FFF9ED` | `colorScheme.surfaceVariant` |
| muted-foreground | `#6B6B6B` | `colorScheme.onSurfaceVariant` |
| destructive | `#D4183D` | `colorScheme.error` |
| destructive-foreground | `#FFFFFF` | `colorScheme.onError` |
| border | `rgba(44,44,44,0.1)` | `colorScheme.outline` / `outlineVariant` |
| ring | `#FFC933` | (maps to primary; used for focus ring) |

### Extended colors (not in Material roles):

| Name | Hex | Usage |
|---|---|---|
| `correctAnswer` | `#4CAF50` | Correct answer highlight |
| `wrongAnswer` | `#D4183D` | Wrong answer highlight |
| `selectedAnswer` | `#FFC933` + 5% alpha bg | Selected but unconfirmed answer |
| `primaryContainer` | `#FFC933` + 20% alpha | Icon backgrounds, subtle highlights |

### Typography scale:

| Style Name | Size | Weight | Line Height | Usage |
|---|---|---|---|---|
| `displayLarge` | 56sp | Bold (700) | 1.1 | Score display (ResultScreen) |
| `headlineLarge` | 32sp | Bold (700) | 1.2 | Splash title |
| `headlineMedium` | 24sp | Bold (700) | 1.3 | Screen titles |
| `headlineSmall` | 20sp | SemiBold (600) | 1.3 | Card titles |
| `bodyLarge` | 16sp | Medium (500) | 1.5 | Question text |
| `bodyMedium` | 14sp | Normal (400) | 1.5 | Descriptions, answer text, buttons |
| `bodySmall` | 12sp | Normal (400) | 1.5 | Captions, footer text |

### Spacing (8dp grid):

| Token | Value |
|---|---|
| `xxs` | 2.dp |
| `xs` | 4.dp |
| `sm` | 8.dp |
| `md` | 12.dp |
| `lg` | 16.dp |
| `xl` | 24.dp |
| `xxl` | 32.dp |
| `xxxl` | 48.dp |

### Shapes:

| Token | Radius | Usage |
|---|---|---|
| `small` | 8.dp | Small badges, chips |
| `medium` | 12.dp | Answer options, buttons, tabs |
| `large` | 16.dp | Cards (~rounded-2xl) |
| `extraLarge` | 24.dp | Large cards, bottom sheets |
| `full` | 50% | Circular (rank badges, avatars) |

---

## Consequences

1. All screens and components must use `MaterialTheme.colorScheme` / `MaterialTheme.typography`
   for standard roles and `AppTheme.spacing` / `AppTheme.extendedColors` for extras.
2. No hardcoded color hex values in screen or component files.
3. Dark mode is **deferred** (light-only for MVP). The theme structure supports adding it later
   by creating a `darkColorScheme` factory.
4. The font family (Inter) is loaded as a Compose resource and set in `AppTypography`.

---

## Follow-ups

- [ ] Implement theme files (Phase 1)
- [ ] Add dark mode color scheme (future phase)
- [ ] Extract design token documentation to `/docs/design-tokens.md` if the palette grows
