/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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