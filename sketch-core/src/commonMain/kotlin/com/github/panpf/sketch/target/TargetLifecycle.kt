package com.github.panpf.sketch.target

import com.github.panpf.sketch.annotation.MainThread
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.jvm.JvmStatic

abstract class TargetLifecycle {

//    protected val observers = mutableSetOf<EventObserver>()

    abstract val currentState: State

    abstract fun addObserver(observer: EventObserver)

    abstract fun removeObserver(observer: EventObserver)

    fun interface EventObserver {
        public fun onStateChanged(source: TargetLifecycle, event: Event)
    }

    public enum class Event {
        /**
         * Constant for onCreate event of the [LifecycleOwner].
         */
        ON_CREATE,

        /**
         * Constant for onStart event of the [LifecycleOwner].
         */
        ON_START,

        /**
         * Constant for onResume event of the [LifecycleOwner].
         */
        ON_RESUME,

        /**
         * Constant for onPause event of the [LifecycleOwner].
         */
        ON_PAUSE,

        /**
         * Constant for onStop event of the [LifecycleOwner].
         */
        ON_STOP,

        /**
         * Constant for onDestroy event of the [LifecycleOwner].
         */
        ON_DESTROY,

        /**
         * An [Event] constant that can be used to match all events.
         */
        ON_ANY;

        /**
         * Returns the new [TargetLifecycle.State] of a [TargetLifecycle] that just reported
         * this [TargetLifecycle.Event].
         *
         * Throws [IllegalArgumentException] if called on [.ON_ANY], as it is a special
         * value used by [OnLifecycleEvent] and not a real lifecycle event.
         *
         * @return the state that will result from this event
         */
        public val targetState: State
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

        public companion object {
            /**
             * Returns the [TargetLifecycle.Event] that will be reported by a [TargetLifecycle]
             * leaving the specified [TargetLifecycle.State] to a lower state, or `null`
             * if there is no valid event that can move down from the given state.
             *
             * @param state the higher state that the returned event will transition down from
             * @return the event moving down the lifecycle phases from state
             */
            @JvmStatic
            public fun downFrom(state: State): Event? {
                return when (state) {
                    State.CREATED -> ON_DESTROY
                    State.STARTED -> ON_STOP
                    State.RESUMED -> ON_PAUSE
                    else -> null
                }
            }

            /**
             * Returns the [TargetLifecycle.Event] that will be reported by a [TargetLifecycle]
             * entering the specified [TargetLifecycle.State] from a higher state, or `null`
             * if there is no valid event that can move down to the given state.
             *
             * @param state the lower state that the returned event will transition down to
             * @return the event moving down the lifecycle phases to state
             */
            @JvmStatic
            public fun downTo(state: State): Event? {
                return when (state) {
                    State.DESTROYED -> ON_DESTROY
                    State.CREATED -> ON_STOP
                    State.STARTED -> ON_PAUSE
                    else -> null
                }
            }

            /**
             * Returns the [TargetLifecycle.Event] that will be reported by a [TargetLifecycle]
             * leaving the specified [TargetLifecycle.State] to a higher state, or `null`
             * if there is no valid event that can move up from the given state.
             *
             * @param state the lower state that the returned event will transition up from
             * @return the event moving up the lifecycle phases from state
             */
            @JvmStatic
            public fun upFrom(state: State): Event? {
                return when (state) {
                    State.INITIALIZED -> ON_CREATE
                    State.CREATED -> ON_START
                    State.STARTED -> ON_RESUME
                    else -> null
                }
            }

            /**
             * Returns the [TargetLifecycle.Event] that will be reported by a [TargetLifecycle]
             * entering the specified [TargetLifecycle.State] from a lower state, or `null`
             * if there is no valid event that can move up to the given state.
             *
             * @param state the higher state that the returned event will transition up to
             * @return the event moving up the lifecycle phases to state
             */
            @JvmStatic
            public fun upTo(state: State): Event? {
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
     * TargetLifecycle states. You can consider the states as the nodes in a graph and
     * [Event]s as the edges between these nodes.
     */
    public enum class State {
        /**
         * Destroyed state for a LifecycleOwner. After this event, this TargetLifecycle will not dispatch
         * any more events. For instance, for an [android.app.Activity], this state is reached
         * **right before** Activity's [onDestroy][android.app.Activity.onDestroy] call.
         */
        DESTROYED,

        /**
         * Initialized state for a LifecycleOwner. For an [android.app.Activity], this is
         * the state when it is constructed but has not received
         * [onCreate][android.app.Activity.onCreate] yet.
         */
        INITIALIZED,

        /**
         * Created state for a LifecycleOwner. For an [android.app.Activity], this state
         * is reached in two cases:
         *
         *  * after [onCreate][android.app.Activity.onCreate] call;
         *  * **right before** [onStop][android.app.Activity.onStop] call.
         *
         */
        CREATED,

        /**
         * Started state for a LifecycleOwner. For an [android.app.Activity], this state
         * is reached in two cases:
         *
         *  * after [onStart][android.app.Activity.onStart] call;
         *  * **right before** [onPause][android.app.Activity.onPause] call.
         *
         */
        STARTED,

        /**
         * Resumed state for a LifecycleOwner. For an [android.app.Activity], this state
         * is reached after [onResume][android.app.Activity.onResume] is called.
         */
        RESUMED;

        /**
         * Compares if this State is greater or equal to the given `state`.
         *
         * @param state State to compare with
         * @return true if this State is greater or equal to the given `state`
         */
        public fun isAtLeast(state: State): Boolean {
            return compareTo(state) >= 0
        }
    }
}

/** Suspend until [TargetLifecycle.currentState] is at least [TargetLifecycle.State.STARTED] */
@MainThread
internal suspend fun TargetLifecycle.awaitStarted() {
    // Fast path: we're already started.
    if (currentState.isAtLeast(TargetLifecycle.State.STARTED)) return

    // Slow path: observe the lifecycle until we're started.
    var observer: TargetLifecycle.EventObserver? = null
    try {
        suspendCancellableCoroutine { continuation ->
            observer = TargetLifecycle.EventObserver { _, event ->
                if (event == TargetLifecycle.Event.ON_START) {
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
fun TargetLifecycle.removeAndAddObserver(observer: TargetLifecycle.EventObserver) {
    removeObserver(observer)
    addObserver(observer)
}