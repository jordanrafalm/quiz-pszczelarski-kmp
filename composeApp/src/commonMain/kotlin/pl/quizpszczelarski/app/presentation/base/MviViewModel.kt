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
     */
    fun onIntent(intent: I) {
        val newState = reduce(_state.value, intent)
        _state.value = newState
    }

    /**
     * Reducer: given current state + intent, produces new state.
     * May call [emitEffect] for one-off side effects (navigation, haptics, etc.).
     * For async work, use [scope] instead.
     */
    protected abstract fun reduce(state: S, intent: I): S

    /**
     * Send a one-off effect (navigation, toast, etc.).
     */
    protected fun emitEffect(effect: E) {
        scope.launch { _effect.send(effect) }
    }

    /**
     * Call when the ViewModel is no longer needed.
     */
    open fun onCleared() {
        scope.cancel()
    }
}
