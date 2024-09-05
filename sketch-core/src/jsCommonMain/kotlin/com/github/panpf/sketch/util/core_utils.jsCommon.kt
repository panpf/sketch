package com.github.panpf.sketch.util

/**
 * Returns true if currently on the main thread
 *
 * @see com.github.panpf.sketch.core.jscommon.test.util.CoreUtilsJsCommonTest.testIsMainThread
 */
internal actual fun isMainThread() = true

/**
 * Throws an exception if not currently on the main thread
 *
 * @see com.github.panpf.sketch.core.jscommon.test.util.CoreUtilsJsCommonTest.testRequiredMainThread
 */
internal actual fun requiredMainThread() {

}

/**
 * Throws an exception if not currently on the work thread
 *
 * @see com.github.panpf.sketch.core.jscommon.test.util.CoreUtilsJsCommonTest.testRequiredWorkThread
 */
internal actual fun requiredWorkThread() {

}