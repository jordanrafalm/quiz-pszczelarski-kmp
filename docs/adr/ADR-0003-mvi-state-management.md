# ADR-0003: MVI State Management for Quiz Screens

> **Date:** 2026-02-17  
> **Status:** Accepted  
> **Supersedes:** Phase 0 decision B1 (MVVM for single screen)  
> **Related:** ADR-0001, architecture_patterns memory

---

## Context

Phase 0 chose "simple MVVM" because there was only one screen with no interactions.
The Quiz app now requires multiple screens with complex state transitions:

- QuizScreen: answer selection → confirmation → next question → finish
- ResultScreen: score calculation, message selection, navigation
- LeaderboardScreen: tab switching, data loading, highlight current user
- HomeScreen: navigation routing

User actions (select answer, confirm, navigate) and state transitions
(selected → confirmed → correct/wrong) are sequential and predictable.
This is a textbook fit for MVI (Model-View-Intent).

---

## Options Considered

### Option A: Keep MVVM with StateFlow

Each ViewModel holds `MutableStateFlow<UiState>` and reacts to events via `onEvent(event)`.
State mutations are scattered across `when` branches in the ViewModel.

**Pros:** Familiar, simple for 2-3 screens.  
**Cons:** State mutations not centralized. No reducer guarantees.
As quiz logic grows (timer, streak, hint), mutation logic fragments.

### Option B: MVI with sealed Intent + Reducer ← **Selected**

Each screen defines:
- `State` — immutable data class
- `Intent` — sealed interface (user actions)
- `Effect` — sealed interface (one-off side effects: navigation, toast)
- Reducer function: `(State, Intent) → State`

ViewModel dispatches intents through the reducer, producing new state.

**Pros:** Predictable state transitions. Easy to test (reducer is pure function).
Scales well for quiz flows with multiple states.  
**Cons:** Slightly more boilerplate per screen. Acceptable for a quiz app.

### Option C: Third-party MVI library (Orbit, MVIKotlin)

Use an established MVI framework.

**Pros:** Production-tested, handles side effects.  
**Cons:** Extra dependency. Overkill for 5 screens. Lock-in risk.

---

## Decision

**Option B: Manual MVI with sealed Intent + Reducer.**

No third-party MVI library. The pattern is simple enough to implement manually
with `StateFlow` + `reduce()` in a base ViewModel.

### Pattern per screen

```
// State
data class QuizState(
    val currentQuestionIndex: Int = 0,
    val questions: List<Question> = emptyList(),
    val selectedAnswerIndex: Int? = null,
    val isAnswerConfirmed: Boolean = false,
    val score: Int = 0,
    val isFinished: Boolean = false,
)

// Intent
sealed interface QuizIntent {
    data class SelectAnswer(val index: Int) : QuizIntent
    data object ConfirmAnswer : QuizIntent
    data object NextQuestion : QuizIntent
    data object FinishQuiz : QuizIntent
}

// Effect (one-off)
sealed interface QuizEffect {
    data class NavigateToResult(val score: Int, val total: Int) : QuizEffect
}
```

### Reducer pattern

```
private fun reduce(state: QuizState, intent: QuizIntent): QuizState =
    when (intent) {
        is QuizIntent.SelectAnswer -> state.copy(selectedAnswerIndex = intent.index)
        is QuizIntent.ConfirmAnswer -> { /* check correct, update score */ }
        is QuizIntent.NextQuestion -> { /* advance index or set isFinished */ }
        is QuizIntent.FinishQuiz -> state.copy(isFinished = true)
    }
```

### Base ViewModel pattern

```
abstract class MviViewModel<S, I, E>(initialState: S) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()
    
    private val _effect = Channel<E>(Channel.BUFFERED)
    val effect: Flow<E> = _effect.receiveAsFlow()

    fun onIntent(intent: I) { /* dispatch to reduce */ }
    protected abstract fun reduce(state: S, intent: I): S
    protected fun emitEffect(effect: E) { /* send to channel */ }
}
```

---

## Why not keep MVVM?

The Phase 0 MVVM decision was for a single gold screen. With quiz state transitions
(select → confirm → reveal → next), MVI's reducer pattern keeps state mutations
predictable and testable. The `onEvent()` function from MVVM is already half-MVI;
adding a formal reducer is a small step.

This is **not a breaking change** — the existing `GoldScreenViewModel` is a placeholder
that will be replaced.

---

## Consequences

1. All new ViewModels follow the MVI pattern (State + Intent + Effect + Reducer).
2. The existing `GoldScreenViewModel` (Phase 0 placeholder) will be removed.
3. Composables are stateless: they receive `State` and emit `Intent` via callbacks.
4. Navigation is triggered via `Effect`, not directly from composables.
5. Reducer functions are pure and unit-testable without coroutines.
6. A lightweight `MviViewModel` base class lives in `composeApp/src/commonMain/.../presentation/base/`.

---

## Follow-ups

- [x] Create `MviViewModel` base class (Phase 1)
- [x] Implement `QuizViewModel`, `HomeViewModel`, `ResultViewModel`
- [x] Remove `GoldScreenViewModel` and `GoldScreen`
