# ADR-0010: Firebase Crashlytics + Analytics in KMP

> **Date:** 2026-02-18  
> **Status:** Accepted  
> **Related:** ADR-0004 (Firebase integration), architecture_patterns, firebase_integration

---

## Context

The app needs:
1. **Crash monitoring** — fatal crash reports + non-fatal exception logging with context (quiz mode, level, question hash).
2. **Basic analytics** — quiz funnel (started → completed vs abandoned), session quiz count, "Regulamin" click tracking.

The project already uses GitLive `firebase-kotlin-sdk` 2.1.0 for Auth, Firestore, and Remote Config.  
Firebase is initialized on both platforms (Android auto-init, iOS `FirebaseApp.configure()`).

The key decision is how to integrate Crashlytics + Analytics in shared KMP code.

---

## Options Considered

### Option A: GitLive `firebase-crashlytics` + `firebase-analytics` ← **Selected**

Extend the existing GitLive dependency set with two additional artifacts:
- `dev.gitlive:firebase-crashlytics:2.1.0`
- `dev.gitlive:firebase-analytics:2.1.0`

**Pros:**
- Consistent with existing Firebase strategy (ADR-0004).
- All analytics/crash code in `commonMain` — zero expect/actual.
- Same version management, same build pipeline.
- Coroutine-friendly API.

**Cons:**
- GitLive crashlytics/analytics modules may have smaller feature surface than native SDK.
- Depends on GitLive maintaining Crashlytics wrapper (less commonly used than Auth/Firestore).

### Option B: Expect/actual wrappers around native SDKs

Write interfaces in `commonMain`, platform-specific implementations calling native
`FirebaseCrashlytics` (Android) and `FIRCrashlytics` (iOS) directly.

**Pros:**
- Full native SDK surface.
- No dependency on GitLive for these modules.

**Cons:**
- Duplicated per-platform code.
- iOS `actual` implementation requires ObjC interop with Crashlytics.
- Maintenance burden for a thin wrapper that GitLive already provides.

### Option C: Third-party crash reporting (Sentry, Bugsnag)

Use a non-Firebase service for crash reporting.

**Pros:**
- Richer crash analysis features (breadcrumbs, releases, session replay).
- Independent from Firebase ecosystem.

**Cons:**
- Additional vendor, additional SDK, additional console.
- Firebase Analytics still needed separately.
- Overkill for MVP; Firebase Crashlytics is sufficient.

---

## Decision

**Option A: GitLive `firebase-crashlytics` + `firebase-analytics`.**

Rationale:
1. Consistent with ADR-0004 — all Firebase behind GitLive in `commonMain`.
2. The app needs basic crash reporting + 4 custom events — GitLive's surface is sufficient.
3. Zero additional expect/actual code.
4. If GitLive's Crashlytics module proves insufficient, migration to expect/actual is scoped to one file (`FirebaseAnalyticsService.kt`).

---

## Consequences

1. **Gradle:** Add `firebase-analytics` and `firebase-crashlytics` GitLive dependencies to `:shared`. Apply `com.google.firebase.crashlytics` plugin to `:composeApp`.
2. **iOS:** Add `FirebaseAnalytics` + `FirebaseCrashlytics` SPM packages. Add dSYM upload Build Phase.
3. **Architecture:** `AnalyticsService` interface in `shared/domain/service/` (pure Kotlin). `FirebaseAnalyticsService` in `shared/data/analytics/`.
4. **Privacy:** No question text in events. Question IDs hashed via `hashCode().toUInt().toString(16)`. No PII beyond Firebase anonymous UID.
5. **ViewModels:** Receive `AnalyticsService` via constructor. Log events at specific lifecycle points (quiz loaded, completed, abandoned, exit).
6. **Non-fatal logging:** All ViewModel `catch` blocks call `recordNonFatal()` with contextual keys.

---

## Risks

| Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|
| GitLive lacks `firebase-crashlytics` artifact | Low | High | Verify on mavenCentral before coding. Fallback: Option B (expect/actual) scoped to one file |
| iOS dSYM symbols missing in Crashlytics | Medium | Medium | Verify Build Phase upload script. Fallback: Fastlane upload |
| GDPR consent not handled | Medium | Medium | MVP: default enabled. Future phase: consent dialog with `.setAnalyticsCollectionEnabled(false)` |

---

## Follow-ups

- [ ] Verify `dev.gitlive:firebase-crashlytics:2.1.0` exists on mavenCentral
- [ ] Verify `dev.gitlive:firebase-analytics:2.1.0` exists on mavenCentral
- [ ] Phase 8+: GDPR consent dialog before enabling analytics collection
- [ ] Add "Regulamin" UI element and wire `terms_clicked` event
