# ADR-0005: Offline Cache Strategy for Questions

> **Date:** 2026-02-17  
> **Status:** Accepted  
> **Related:** ADR-0004 (Firebase integration), data_strategy, architecture_patterns

---

## Context

Phase 2 introduced Firebase Firestore as the question source. Every quiz start fetches all active
questions from Firestore. This has two problems:

1. **No offline support.** If the device is offline, the quiz fails to load (depends on Firestore SDK
   built-in cache, which is unreliable for KMP via GitLive and not explicitly managed).
2. **Unnecessary reads.** Questions change infrequently, yet every app launch re-reads the full
   collection. Firestore charges per document read.

Phase 3 goal: cache questions locally so the app starts instantly from cache, refreshes in the
background when needed, and works fully offline.

### Constraints

- Kotlin 2.2.21, Compose Multiplatform 1.10.1
- Clean Architecture (presentation → domain ← data) — domain layer must not change its interface
- MVI pattern — state transitions must remain deterministic
- Question count at MVP: ~50–200. Expected ceiling: ~500.
- Quiz run determinism: once a quiz starts, the question set must be immutable for that run.

---

## Decisions

### Decision 1: Local Storage — SQLDelight

**Selected: SQLDelight** (over DataStore/file cache)

| Option | Pros | Cons |
|---|---|---|
| **SQLDelight** | Typed queries, schema migration support, multiplatform (Android+iOS native drivers), proper indexing for filtered queries | Plugin + driver setup overhead |
| File cache (JSON) | Zero setup | No query support, manual serialization, no migration path |
| Multiplatform Settings / DataStore | Simple key-value | Not suited for structured data with filtering |

SQLDelight is the right tool because questions need filtered queries (`level`, `category`, `active`)
and the schema will grow. Setup cost is one-time.

### Decision 2: Refresh Strategy — Meta Document

**Selected: Option A — Global metadata document** (`questionsMeta/snapshot`)

Firestore document: `questionsMeta/snapshot`
```json
{
  "version": 3,
  "updatedAt": Timestamp("2026-02-17T12:00:00Z"),
  "questionCount": 127
}
```

**Sync algorithm:**
1. Read `questionsMeta/snapshot` (1 Firestore read).
2. Compare `version` to locally stored `lastSyncVersion`.
3. If equal → skip. If different → fetch all active questions + store.
4. Store new `lastSyncVersion` + `lastSyncAt` locally.

**Why not per-question `updatedAt` incremental sync (Option B):**
- At MVP scale (<500 questions), full re-download on version change is cheap (~500 reads, rare).
- Option B requires: composite index, merge/upsert logic in SQLDelight, soft-delete tracking.
- Option A is simpler to implement, test, and debug.
- Migration to Option B is straightforward later: add `updatedAt` column, change sync query.

### Decision 3: Data Source Split

The `QuestionRepository` domain interface stays unchanged. The data layer internally splits into:

- **`RemoteQuestionsDataSource`** — wraps Firestore calls (questions collection + meta doc).
- **`LocalQuestionsDataSource`** — wraps SQLDelight reads/writes.
- **`CachingQuestionsRepository`** — orchestrates cache-first loading + background sync.
  Implements `QuestionRepository`.

### Decision 4: Platform Drivers (expect/actual)

SQLDelight needs a `SqlDriver` factory:
- **Android:** `AndroidSqliteDriver(schema, context, "quiz.db")`
- **iOS:** `NativeSqliteDriver(schema, "quiz.db")`

This is the only expect/actual surface for this feature:
`expect fun createDatabaseDriver(): SqlDriver` in `shared/commonMain`,
`actual` in `shared/androidMain` and `shared/iosMain`.

---

## Consequences

1. **Gradle:** Add SQLDelight plugin + Android/iOS/native drivers to `:shared`.
2. **Schema:** `.sq` file(s) in `shared/src/commonMain/sqldelight/`.
3. **Data layer:** New `local/` and `remote/` packages. `CachingQuestionsRepository` replaces
   `FirebaseQuestionsRepository` as the `QuestionRepository` implementation wired in AppNavigation.
4. **Expect/actual:** `DatabaseDriverFactory` in `shared/{androidMain,iosMain}`.
5. **Domain:** No changes to `QuestionRepository` interface.
6. **Presentation:** `QuizViewModel` gains offline-aware states (`isOffline`, `isRefreshing`).
   New effects for snackbar messages.
7. **Firestore:** New `questionsMeta` collection with `snapshot` document. Must be created and
   maintained when questions are added/modified (admin concern, not app concern).
8. **Leaderboard:** Unaffected. Real-time Firestore listener continues as-is.
9. **User/Auth:** Unaffected.

---

## Risks

| Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|
| SQLDelight + Kotlin 2.2.21 compatibility | Low | High | Verify with `2.0.2` (latest stable). Fallback: downgrade to last compatible. |
| First-launch cold start (empty cache + offline) | Medium | Medium | Show "No cached questions" error with retry button. |
| Meta doc not updated when questions change | Medium | Medium | Document admin procedure. Consider Cloud Function trigger. |
| SQLDelight schema migration on update | Low | Low | SQLDelight has built-in migration support via numbered `.sqm` files. |
| GitLive Firestore `get()` vs SDK offline cache conflict | Low | Low | We explicitly manage offline via SQLDelight; don't rely on Firestore SDK cache. |

---

## Follow-ups

- [ ] Create `questionsMeta/snapshot` document in Firestore Console (or via admin script)
- [ ] Write Phase 3 implementation plan (`plans/phase-3-offline-cache.md`)
- [ ] Update `data_strategy` memory with SQLDelight + caching details
- [ ] Verify SQLDelight 2.0.2 compatibility with Kotlin 2.2.21
- [ ] Consider Cloud Function to auto-update `questionsMeta/snapshot.version` on question writes
