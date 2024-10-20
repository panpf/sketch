package com.github.panpf.sketch.util

/**
 * Returns true if currently on the main thread
 *
 * @see com.github.panpf.sketch.core.ios.test.util.CoreUtilsIosTest.testIsMainThread
 */
internal actual fun isMainThread() = platform.Foundation.NSThread.isMainThread

/**
 * Throws an exception if not currently on the main thread
 *
 * @see com.github.panpf.sketch.core.ios.test.util.CoreUtilsIosTest.testRequiredMainThread
 */
internal actual fun requiredMainThread() {
    check(isMainThread()) {
        "This method must be executed in the UI thread"
    }
}

/**
 * Throws an exception if not currently on the work thread
 *
 * @see com.github.panpf.sketch.core.ios.test.util.CoreUtilsIosTest.testRequiredWorkThread
 */
actual fun requiredWorkThread() {
    check(!isMainThread()) {
        "This method must be executed in the work thread"
    }
}