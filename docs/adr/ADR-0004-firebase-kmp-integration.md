# ADR-0004: Firebase Integration Approach for KMP

> **Date:** 2026-02-17  
> **Status:** Accepted  
> **Related:** ADR-0001, architecture_patterns, data_strategy, modular_architecture

---

## Context

Phase 2 requires Firebase Auth (anonymous) and Cloud Firestore for:
- anonymous user identity (UID-based)
- fetching quiz questions from Firestore
- global leaderboard (cumulative totalScore per user)

The project uses Kotlin 2.2.21, Compose Multiplatform 1.10.1, and targets Android + iOS.
Firebase logic must be confined to the data layer (`shared/src/commonMain` data package), 
behind domain repository interfaces. No Firebase types may leak outside `data`.

The key decision is **how to call Firebase from shared Kotlin code**.

---

## Options Considered

### Option A: GitLive `firebase-kotlin-sdk` ← **Selected**

A community-maintained KMP wrapper around native Firebase SDKs.
Artifacts: `dev.gitlive:firebase-auth`, `dev.gitlive:firebase-firestore`.

**Pros:**
- True multiplatform API in `commonMain` — zero expect/actual for Auth + Firestore.
- First-class Kotlin coroutines + `Flow` for real-time listeners.
- `kotlinx-serialization` integration for encoding/decoding documents.
- Minimal boilerplate: one `FirebaseQuestionsRepository` for both platforms.
- Actively maintained; supports Kotlin 2.x.

**Cons:**
- Third-party dependency — version lag vs official SDK releases.
- Subset of official SDK surface (sufficient for Auth anonymous + Firestore CRUD/queries).
- Adds transitive dependency on native Firebase SDKs for each platform.

### Option B: Expect/actual wrappers with native Firebase SDKs

Write `expect` interfaces in `commonMain`, `actual` implementations calling
`com.google.firebase` on Android and `FirebaseFirestore` via Swift/ObjC interop on iOS.

**Pros:**
- Always latest official SDK version.
- Full feature surface.

**Cons:**
- Duplicated implementation per platform (~2x code).
- iOS actual requires Kotlin/Native interop with ObjC Firebase SDK (complex, fragile).
- Manual coroutine bridging for iOS callbacks.
- Significantly higher development and maintenance cost.

### Option C: Firebase REST API (no SDK)

Call Firestore/Auth via HTTP (Ktor) — fully in commonMain.

**Pros:**
- No native SDK dependency at all.
- Simpler build config.

**Cons:**
- Lose offline persistence, real-time listeners, automatic token refresh.
- Must manage auth tokens manually (refresh, expiry).
- Real-time leaderboard requires long-polling or SSE — complex.
- Not idiomatic for Firebase.

---

## Decision

**Option A: GitLive `firebase-kotlin-sdk`.**

Rationale:
1. The app needs only Auth (anonymous sign-in) and Firestore (queries + real-time listeners) — both well-supported.
2. Eliminates expect/actual overhead — repositories live entirely in `commonMain`.
3. Flow-based snapshot listeners integrate naturally with MVI architecture.
4. The team values speed and minimal boilerplate for MVP.
5. If GitLive falls behind, migration to expect/actual is scoped to the data layer only.

### Library versions (at time of writing)

```
dev.gitlive:firebase-auth:2.1.0
dev.gitlive:firebase-firestore:2.1.0
```

> Pin to a single version. Update only via a dedicated dependency-update PR.

---

## Consequences

1. **Gradle:** Add GitLive firebase-auth and firebase-firestore to `:shared` `commonMain.dependencies`.
2. **Android:** Apply `com.google.gms.google-services` plugin to `:composeApp`. Move `google-services.json` into `composeApp/`.
3. **iOS:** Copy `GoogleService-Info.plist` into `iosApp/iosApp/` bundle. Initialize Firebase in `iOSApp.swift` via `FirebaseApp.configure()`. Add Firebase SPM dependencies for iOS native layer.
4. **Data layer:** All Firebase calls in `shared/src/commonMain/.../data/firebase/`, `data/questions/`, `data/user/`, `data/leaderboard/`.
5. **Domain layer:** Repository interfaces + models remain pure Kotlin. No Firebase imports.
6. **Presentation:** ViewModels receive repositories via constructor. No Firebase awareness.
7. **Internet permission:** Add `<uses-permission android:name="android.permission.INTERNET"/>` to Android manifest.
8. **Firestore offline:** Rely on GitLive/Firebase built-in offline cache for reads. No custom caching layer.

---

## Risks

| Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|
| GitLive version lag on Kotlin 2.2.x | Low | Medium | Pin working version; fallback to expect/actual scoped to data layer |
| GitLive API differences from native SDK | Low | Low | Only using basic Auth + Firestore features |
| iOS Firebase SDK version conflict with GitLive | Low | Medium | Lock Firebase iOS SDK version via SPM to match GitLive's transitive dependency |
| Offline sync edge cases (Firestore cache) | Medium | Low | Accept Firebase's default offline behavior; no custom caching |

---

## Follow-ups

- [ ] Verify GitLive 2.1.0 compiles with Kotlin 2.2.21 and Compose MP 1.10.1
- [ ] Write Phase 2 implementation plan (`plans/phase-2-firebase-integration.md`)
- [ ] Create `data_strategy` memory update with Firebase implementation details
- [ ] Update `modular_architecture` memory with `:shared:data` Firebase packages
