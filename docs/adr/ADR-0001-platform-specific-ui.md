# ADR-0001: Compose Multiplatform Shared UI

> **Date:** 2026-02-16  
> **Status:** Accepted

---

## Context

Quiz Pszczelarski is a greenfield KMP app targeting Android and iOS.  
A decision is needed on how to implement the UI layer.

---

## Decision

**Use Compose Multiplatform shared UI.**

All screens and components live in `composeApp/src/commonMain`. Platform entrypoints (`androidMain`, `iosMain`) contain only minimal bootstrap glue.

---

## Rationale

- Maximum code sharing — single screen implementation for both platforms.
- Growing ecosystem with strong JetBrains support.
- No need for iOS-specific bridging (SKIE, manual wrappers).
- Simpler module structure — one `composeApp` module with shared screens.
- Fewer files to maintain; faster MVP delivery.

---

## Consequences

1. **Module structure:** `composeApp` (shared UI + platform entrypoints) + `:shared:domain` + `:shared:data` + optional `:shared:presentation`.
2. **iOS host:** minimal — just bootstraps the shared Compose UI.
3. **Compose Multiplatform plugin** required in Gradle.
4. **Platform-native look & feel trade-off:** accepted at MVP stage; can be refined with Material 3 adaptive theming later.
5. **No SKIE / iOS bridge needed** — ViewModels consumed directly in Compose.
