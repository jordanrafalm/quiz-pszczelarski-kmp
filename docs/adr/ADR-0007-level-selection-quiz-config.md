# ADR-0007: Level Selection and Quiz Configuration

> **Status:** Accepted
> **Date:** 2026-02-17
> **Deciders:** Architect
> **Relates to:** ADR-0003 (MVI), ADR-0005 (Offline Cache)

---

## Context

Users need to choose between difficulty levels ("Normalny" and "Pro dla Pszczelarzy") and configure the number of questions before starting a quiz. This requires changes to routing, state management, and the question retrieval pipeline.

Key design decisions:
1. Where does level selection UI live — new screen/route or inline in HomeScreen?
2. How is level + count passed through the stack (Route → ViewModel → UseCase → Repository)?
3. How do we handle edge cases (no questions for a level, insufficient count)?

---

## Options Considered

### Option A: New Route (`Route.ModeSelect`)

- Add a separate `ModeSelectScreen` with its own Route
- Pro: Clean separation, independent ViewModel
- Con: Extra navigation step, more files, animation between Home → ModeSelect → Quiz feels heavy

### Option B: Inline sub-state in HomeScreen (selected)

- `HomeState.showLevelSelect: Boolean` toggles between main content and level picker
- Use `AnimatedContent` within HomeScreen for smooth transition
- Pro: No new route, single ViewModel, smoother UX (no full-screen transition)
- Con: HomeState/ViewModel slightly more complex

### Option C: Bottom sheet / dialog

- Show level selection as a modal bottom sheet
- Pro: No state complexity, overlay pattern
- Con: Bottom sheets in Compose Multiplatform can be inconsistent across platforms

---

## Decision

**Option B — Inline sub-state in HomeScreen.**

Rationale:
- Avoids a new route and the navigation overhead
- `AnimatedContent` provides smooth transition between main content and level picker
- Keeps the flow within a single screen, which matches the user's mental model ("I'm still on the home screen, just choosing options")
- HomeViewModel is currently almost stateless; adding level selection fields is minimal complexity

---

## Consequences

### Route Change

`Route.Quiz` changes from `data object` to `data class`:
```kotlin
data class Quiz(val level: String, val questionCount: Int = 5) : Route
```

This is a **breaking change** for all `Route.Quiz` references. All usages must be updated to pass `level` and `questionCount`.

### Data Flow

```
HomeScreen → HomeVM (SelectLevel intent) → Effect(NavigateToQuiz(level, count))
  → AppNavigation → Route.Quiz(level, count)
    → QuizViewModel(level, count) → GetRandomQuestionsUseCase(count, level)
      → QuestionRepository.getActiveQuestions(level = level)
        → SQLDelight/Firestore (WHERE level = ?)
```

### Question Data Requirement

Firestore `questions` collection documents MUST have a `level` field with values `"easy"` or `"pro"`. The `SQLDelight` cache already stores `level` via `QuestionEntity`. If `level` is not set on some questions, they will be invisible to filtered queries.

**Fallback:** If a level filter returns an empty list, show all questions regardless of level and notify the user via snackbar.

### Question Count

- Default: 5
- Options: 5, 10, 15, 20
- If fewer questions exist than requested, use all available (no error)
- Count is NOT persisted — resets to 5 each time the app launches (MVP simplicity)
- Future: persist last selected count in `SettingsRepository`

---

## Follow-ups

- Verify all Firestore questions have `level` field populated
- Consider persisting `selectedQuestionCount` in settings (post-MVP)
- Consider adding more levels or custom question count input (post-MVP)
- If level selection grows in complexity, revisit Option A (separate route)
