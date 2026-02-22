# ADR-0011: Answer Feedback + Bonus Streak in Quiz Flow

> **Status:** Accepted  
> **Date:** 2026-02-21  
> **Context:** Phase 8 — Tryb "Zabawa"

---

## Context

The quiz currently follows a single-phase answer flow: user selects an answer → clicks "Dalej" → moves to the next question. The user receives **no immediate feedback** about whether their answer was correct or wrong. Score is only visible on the ResultScreen.

Product requirements demand:
1. **Immediate visual feedback** after checking an answer (green/red blink)
2. **Correct answer reveal** when the user's answer is wrong
3. **Bonus overlay** ("ALE ŻĄDLISZ WIEDZĄ!") after 3 consecutive correct answers (max 1× per quiz session)
4. **Infotip display** (educational explanation) after wrong answers
5. **Two-phase button**: "Sprawdź" (check) → "Dalej" (next)

This changes the quiz flow from 1-phase (select → next) to 2-phase (select → check → next).

---

## Options Considered

### Option A: Single-phase with inline feedback (rejected)

Keep `NextQuestion` doing everything — score, feedback animation, then auto-advance after delay.

**Pros:** Minimal state changes.  
**Cons:** Mixing scoring + navigation in one step makes it hard to show feedback before advancing. Requires a timed delay in the reducer (impure). User can't read infotip at their own pace.

### Option B: Two-phase with `CheckAnswer` intent (selected)

Introduce a new `CheckAnswer` intent that:
- Evaluates the selected answer
- Sets feedback state (Correct/Wrong)
- Updates score and streak
- Optionally triggers bonus overlay
- Blocks further answer selection

`NextQuestion` then only advances to the next question (or finishes the quiz), resetting feedback state.

**Pros:**
- Clean separation: checking vs. advancing
- User controls pace (reads infotip, sees feedback)
- Streak/bonus logic is pure (in reducer), fully testable
- No timers in business logic
- `AnswerOption` already has `Correct`/`Wrong` states with animations — just needs render mapping

**Cons:** More state fields. Two button labels needed.

### Option C: Separate "feedback screen" (rejected)

Navigate to a feedback screen between questions.

**Pros:** Clear UX boundary.  
**Cons:** Over-engineered for the feature. Breaks quiz flow. Adds navigation complexity.

---

## Decision

**Option B: Two-phase `CheckAnswer` / `NextQuestion` flow.**

### State additions to `QuizState`

| Field | Type | Purpose |
|---|---|---|
| `answerFeedback` | `AnswerFeedback?` | null = not checked yet; Correct/Wrong after check |
| `correctAnswerIndex` | `Int?` | Index of correct answer (shown on wrong) |
| `consecutiveCorrect` | `Int` | Running streak counter |
| `bonusShown` | `Boolean` | Guard: bonus shown max 1× per session |
| `showBonus` | `Boolean` | Currently showing bonus overlay |
| `currentInfotip` | `String?` | Infotip text from question (when wrong + non-blank) |

### New intents

- `CheckAnswer` — evaluates answer, updates score/streak/feedback
- `DismissBonus` — hides bonus overlay (auto-triggered after animation)

### Computed property changes

- `canProceed` now requires `answerFeedback != null` (not just `selectedAnswerIndex != null`)
- New `isAnswerChecked: Boolean` = `answerFeedback != null`

### Bonus rules

- Triggered when `consecutiveCorrect` reaches 3 AND `bonusShown` is false
- `bonusShown` is set to `true` permanently for the quiz session (reset only on new quiz)
- Overlay auto-dismisses after 3.5s via `DismissBonus` intent
- During bonus overlay: answer options disabled, but exit button remains accessible

---

## Consequences

### Positive
- Clean, testable streak/bonus logic (pure reducer)
- Existing `AnswerOptionState.Correct/Wrong` finally used
- Infotip provides educational value per question
- User-controlled pace (no auto-advance timers)

### Negative
- `QuizState` grows by 6 fields
- `QuizViewModel.reduce()` gains complexity (but each branch is simple)
- Requires Firestore data: `infotip` field populated, `level: "mid"/"hard"` values

### Risks
- Firestore `level: "pro"` → `"hard"` migration needed before release
- Questions with `level: "mid"` must be created before "Technik" level is useful

---

## Follow-ups

- Firestore admin migration: `"pro"` → `"hard"` level renaming
- Content: add `level: "mid"` questions for ROL.03/ROL.09
- Android adaptive icon: foreground PNG generation (Image Asset Studio)
- Future: consider configuring bonus streak threshold via Remote Config
