package pl.quizpszczelarski.app.presentation.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Base MVI ViewModel (ADR-0003).
 *
 * @param S Immutable state data class.
 * @param I Sealed intent interface (user actions).
 * @param E Sealed effect interface (one-off side effects).
 * @param initialState Starting state.
 */
abstract class MviViewModel<S, I, E>(initialState: S) {

    protected val scope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effect = Channel<E>(Channel.BUFFERED)
    val effect: Flow<E> = _effect.receiveAsFlow()

    /**
     * Entry point for all user actions.
     * Dispatches intent to reducer, updates state, then processes any queued effects.
     */
    fun onIntent(intent: I) {
        val newState = reduce(_state.value, intent)
        _state.value = newState
    }

    /**
     * Reducer: given current state + intent, produces new state.
     *
     * Side effects (navigation, haptics, snackbars, etc.) should be emitted via [emitEffect].
     * Effects are queued asynchronously and delivered to UI in order.
     *
     * Reducer must be deterministic: same (state, intent) → same new state.
     * However, effects can be emitted as a side effect (queued, not awaited).
     */
    protected abstract fun reduce(state: S, intent: I): S

    /**
     * Queue a one-off effect (navigation, toast, haptic, etc.) for delivery to UI.
     * Effects are buffered and consumed by UI collectors in FIFO order.
     *
     * Note: Effect delivery is asynchronous relative to state update.
     * UI should observe both state and effect flows to handle all changes.
     */
    protected fun emitEffect(effect: E) {
        scope.launch { _effect.send(effect) }
    }

    /**
     * Call when the ViewModel is no longer needed (screen destroyed, etc.).
     * Cancels all pending coroutines and prevents memory leaks.
     */
    open fun onCleared() {
        scope.cancel()
    }
}
