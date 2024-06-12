package com.github.panpf.sketch.lifecycle

/**
 * Marks a class as a LifecycleObserver. Don't use this interface directly. Instead implement either
 * [DefaultPlatformLifecycleObserver] or [PlatformLifecycleEventObserver] to be notified about
 * lifecycle events.
 *
 * @see PlatformLifecycle Lifecycle - for samples and usage patterns.
 */
interface PlatformLifecycleObserver

/**
 * Class that can receive any lifecycle change and dispatch it to the receiver.
 *
 * If a class implements both this interface and
 * [androidx.lifecycle.DefaultLifecycleObserver], then
 * methods of `DefaultLifecycleObserver` will be called first, and then followed by the call
 * of [PlatformLifecycleEventObserver.onStateChanged]
 */
fun interface PlatformLifecycleEventObserver : PlatformLifecycleObserver {
    /**
     * Called when a state transition event happens.
     *
     * @param source The source of the event
     * @param event The event
     */
    fun onStateChanged(source: PlatformLifecycleOwner, event: PlatformLifecycle.Event)
}

/**
 * Callback interface for listening to [PlatformLifecycleOwner] state changes.
 * If a class implements both this interface and [PlatformLifecycleEventObserver], then
 * methods of `DefaultLifecycleObserver` will be called first, and then followed by the call
 * of [PlatformLifecycleEventObserver.onStateChanged]
 */
interface DefaultPlatformLifecycleObserver : PlatformLifecycleObserver {

    /**
     * Notifies that `ON_CREATE` event occurred.
     *
     *
     * This method will be called after the [PlatformLifecycleOwner]'s `onCreate`
     * method returns.
     *
     * @param owner the component, whose state was changed
     */
    fun onCreate(owner: PlatformLifecycleOwner) {}

    /**
     * Notifies that `ON_START` event occurred.
     *
     *
     * This method will be called after the [PlatformLifecycleOwner]'s `onStart` method returns.
     *
     * @param owner the component, whose state was changed
     */
    fun onStart(owner: PlatformLifecycleOwner) {}

    /**
     * Notifies that `ON_RESUME` event occurred.
     *
     *
     * This method will be called after the [PlatformLifecycleOwner]'s `onResume`
     * method returns.
     *
     * @param owner the component, whose state was changed
     */
    fun onResume(owner: PlatformLifecycleOwner) {}

    /**
     * Notifies that `ON_PAUSE` event occurred.
     *
     *
     * This method will be called before the [PlatformLifecycleOwner]'s `onPause` method
     * is called.
     *
     * @param owner the component, whose state was changed
     */
    fun onPause(owner: PlatformLifecycleOwner) {}

    /**
     * Notifies that `ON_STOP` event occurred.
     *
     *
     * This method will be called before the [PlatformLifecycleOwner]'s `onStop` method
     * is called.
     *
     * @param owner the component, whose state was changed
     */
    fun onStop(owner: PlatformLifecycleOwner) {}

    /**
     * Notifies that `ON_DESTROY` event occurred.
     *
     *
     * This method will be called before the [PlatformLifecycleOwner]'s `onDestroy` method
     * is called.
     *
     * @param owner the component, whose state was changed
     */
    fun onDestroy(owner: PlatformLifecycleOwner) {}
}