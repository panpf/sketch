package com.github.panpf.sketch.util

import javax.swing.SwingUtilities


/**
 * Returns true if currently on the main thread
 *
 * @see com.github.panpf.sketch.core.desktop.test.util.CoreUtilsDesktopTest.testIsMainThread
 */
internal actual fun isMainThread() = SwingUtilities.isEventDispatchThread()

/**
 * Throws an exception if not currently on the main thread
 *
 * @see com.github.panpf.sketch.core.desktop.test.util.CoreUtilsDesktopTest.testRequiredMainThread
 */
internal actual fun requiredMainThread() {
    check(isMainThread()) {
        "This method must be executed in the UI thread"
    }
}

/**
 * Throws an exception if not currently on the work thread
 *
 * @see com.github.panpf.sketch.core.desktop.test.util.CoreUtilsDesktopTest.testRequiredWorkThread
 */
actual fun requiredWorkThread() {
    check(!isMainThread()) {
        "This method must be executed in the work thread"
    }
}