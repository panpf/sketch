package com.github.panpf.sketch.lifecycle

import com.github.panpf.sketch.annotation.MainThread
import com.github.panpf.sketch.lifecycle.PlatformLifecycle.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.jvm.JvmStatic

/**
 * Defines an object that has an Android Lifecycle. Fragment
 * and FragmentActivity classes implement
 * [PlatformLifecycleOwner] interface which has the [ getLifecycle][PlatformLifecycleOwner.lifecycle] method to access the Lifecycle. You can also implement [PlatformLifecycleOwner]
 * in your own classes.
 *
 * [Event.ON_CREATE], [Event.ON_START], [Event.ON_RESUME] events in this class
 * are dispatched **after** the [PlatformLifecycleOwner]'s related method returns.
 * [Event.ON_PAUSE], [Event.ON_STOP], [Event.ON_DESTROY] events in this class
 * are dispatched **before** the [PlatformLifecycleOwner]'s related method is called.
 * For instance, [Event.ON_START] will be dispatched after
 * onStart returns, [Event.ON_STOP] will be dispatched
 * before onStop is called.
 * This gives you certain guarantees on which state the owner is in.
 *
 * To observe lifecycle events call [.addObserver] passing an object
 * that implements either [DefaultPlatformLifecycleObserver] or [PlatformLifecycleEventObserver].
 */
abstract class PlatformLifecycle {

    /**
     * Returns the current state of the Lifecycle.
     *
     * @return The current state of the Lifecycle.
     */
    abstract val currentState: State

    /**
     * Returns a [StateFlow] where the [StateFlow.value] represents
     * the current [State] of this Lifecycle.
     *
     * @return [StateFlow] where the [StateFlow.value] represents
     * the current [State] of this Lifecycle.
     */
    open val currentStateFlow: StateFlow<State>
        get() {
            val mutableStateFlow = MutableStateFlow(currentState)
            PlatformLifecycleEventObserver { _, event ->
                mutableStateFlow.value = event.targetState
            }.also { addObserver(it) }
            return mutableStateFlow.asStateFlow()
        }

    /**
     * Adds a LifecycleObserver that will be notified when the PlatformLifecycleOwner changes
     * state.
     *
     * The given observer will be brought to the current state of the PlatformLifecycleOwner.
     * For example, if the PlatformLifecycleOwner is in [State.STARTED] state, the given observer
     * will receive [Event.ON_CREATE], [Event.ON_START] events.
     *
     * @param observer The observer to notify.
     */
    abstract fun addObserver(observer: PlatformLifecycleObserver)

    /**
     * Removes the given observer from the observers list.
     *
     * If this method is called while a state change is being dispatched,
     *
     *  * If the given observer has not yet received that event, it will not receive it.
     *  * If the given observer has more than 1 method that observes the currently dispatched
     * event and at least one of them received the event, all of them will receive the event and
     * the removal will happen afterwards.
     *
     *
     * @param observer The observer to be removed.
     */
    abstract fun removeObserver(observer: PlatformLifecycleObserver)

    enum class Event {
        /**
         * Constant for onCreate event of the [PlatformLifecycleOwner].
         */
        ON_CREATE,

        /**
         * Constant for onStart event of the [PlatformLifecycleOwner].
         */
        ON_START,

        /**
         * Constant for onResume event of the [PlatformLifecycleOwner].
         */
        ON_RESUME,

        /**
         * Constant for onPause event of the [PlatformLifecycleOwner].
         */
        ON_PAUSE,

        /**
         * Constant for onStop event of the [PlatformLifecycleOwner].
         */
        ON_STOP,

        /**
         * Constant for onDestroy event of the [PlatformLifecycleOwner].
         */
        ON_DESTROY,

        /**
         * An [Event] constant that can be used to match all events.
         */
        ON_ANY;

        /**
         * Returns the new [PlatformLifecycle.State] of a [PlatformLifecycle] that just reported
         * this [PlatformLifecycle.Event].
         *
         * @return the state that will result from this event
         */
        val targetState: State
            get() {
                when (this) {
                    ON_CREATE, ON_STOP -> return State.CREATED
                    ON_START, ON_PAUSE -> return State.STARTED
                    ON_RESUME -> return State.RESUMED
                    ON_DESTROY -> return State.DESTROYED
                    ON_ANY -> {}
                }
                throw IllegalArgumentException("$this has no target state")
            }

        companion object {
            /**
             * Returns the [PlatformLifecycle.Event] that will be reported by a [PlatformLifecycle]
             * leaving the specified [PlatformLifecycle.State] to a lower state, or `null`
             * if there is no valid event that can move down from the given state.
             *
             * @param state the higher state that the returned event will transition down from
             * @return the event moving down the lifecycle phases from state
             */
            @JvmStatic
            fun downFrom(state: State): Event? {
                return when (state) {
                    State.CREATED -> ON_DESTROY
                    State.STARTED -> ON_STOP
                    State.RESUMED -> ON_PAUSE
                    else -> null
                }
            }

            /**
             * Returns the [PlatformLifecycle.Event] that will be reported by a [PlatformLifecycle]
             * entering the specified [PlatformLifecycle.State] from a higher state, or `null`
             * if there is no valid event that can move down to the given state.
             *
             * @param state the lower state that the returned event will transition down to
             * @return the event moving down the lifecycle phases to state
             */
            @JvmStatic
            fun downTo(state: State): Event? {
                return when (state) {
                    State.DESTROYED -> ON_DESTROY
                    State.CREATED -> ON_STOP
                    State.STARTED -> ON_PAUSE
                    else -> null
                }
            }

            /**
             * Returns the [PlatformLifecycle.Event] that will be reported by a [PlatformLifecycle]
             * leaving the specified [PlatformLifecycle.State] to a higher state, or `null`
             * if there is no valid event that can move up from the given state.
             *
             * @param state the lower state that the returned event will transition up from
             * @return the event moving up the lifecycle phases from state
             */
            @JvmStatic
            fun upFrom(state: State): Event? {
                return when (state) {
                    State.INITIALIZED -> ON_CREATE
                    State.CREATED -> ON_START
                    State.STARTED -> ON_RESUME
                    else -> null
                }
            }

            /**
             * Returns the [PlatformLifecycle.Event] that will be reported by a [PlatformLifecycle]
             * entering the specified [PlatformLifecycle.State] from a lower state, or `null`
             * if there is no valid event that can move up to the given state.
             *
             * @param state the higher state that the returned event will transition up to
             * @return the event moving up the lifecycle phases to state
             */
            @JvmStatic
            fun upTo(state: State): Event? {
                return when (state) {
                    State.CREATED -> ON_CREATE
                    State.STARTED -> ON_START
                    State.RESUMED -> ON_RESUME
                    else -> null
                }
            }
        }
    }

    /**
     * PlatformLifecycle states. You can consider the states as the nodes in a graph and
     * [Event]s as the edges between these nodes.
     */
    enum class State {
        /**
         * Destroyed state for a PlatformLifecycleOwner. After this event, this PlatformLifecycle will not dispatch
         * any more events. For instance, for an 'android.app.Activity', this state is reached
         * **right before** Activity's onDestroy call.
         */
        DESTROYED,

        /**
         * Initialized state for a PlatformLifecycleOwner. For an 'android.app.Activity', this is
         * the state when it is constructed but has not received
         * onCreate yet.
         */
        INITIALIZED,

        /**
         * Created state for a PlatformLifecycleOwner. For an 'android.app.Activity', this state
         * is reached in two cases:
         *
         *  * after onCreate call;
         *  * **right before** [onStop]['android.app.Activity'.onStop] call.
         *
         */
        CREATED,

        /**
         * Started state for a PlatformLifecycleOwner. For an 'android.app.Activity', this state
         * is reached in two cases:
         *
         *  * after onStart call;
         *  * **right before** onPause call.
         *
         */
        STARTED,

        /**
         * Resumed state for a PlatformLifecycleOwner. For an 'android.app.Activity', this state
         * is reached after onResume is called.
         */
        RESUMED;

        /**
         * Compares if this State is greater or equal to the given `state`.
         *
         * @param state State to compare with
         * @return true if this State is greater or equal to the given `state`
         */
        fun isAtLeast(state: State): Boolean {
            return compareTo(state) >= 0
        }
    }
}

/** Suspend until [PlatformLifecycle.currentState] is at least [PlatformLifecycle.State.STARTED] */
@MainThread
internal suspend fun PlatformLifecycle.awaitStarted() {
    // Fast path: we're already started.
    if (currentState.isAtLeast(PlatformLifecycle.State.STARTED)) return

    // Slow path: observe the lifecycle until we're started.
    var observer: PlatformLifecycleEventObserver? = null
    try {
        suspendCancellableCoroutine { continuation ->
            observer = PlatformLifecycleEventObserver { _, event ->
                if (event == PlatformLifecycle.Event.ON_START) {
                    continuation.resume(kotlin.Unit)
                }
            }
            addObserver(observer!!)
        }
    } finally {
        // 'observer' will always be null if this method is marked as 'inline'.
        observer?.let(::removeObserver)
    }
}

/** Remove and re-add the observer to ensure all its lifecycle callbacks are invoked. */
@MainThread
fun PlatformLifecycle.removeAndAddObserver(observer: PlatformLifecycleObserver) {
    removeObserver(observer)
    addObserver(observer)
}