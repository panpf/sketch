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

import com.github.panpf.sketch.Sketch

/**
 * Create a platform-specific [SystemCallbacks] instance
 *
 * @see com.github.panpf.sketch.core.android.test.util.SystemCallbacksAndroidTest.testSystemCallbacks
 * @see com.github.panpf.sketch.core.desktop.test.util.SystemCallbacksDesktopTest.testSystemCallbacks
 * @see com.github.panpf.sketch.core.jscommon.test.util.SystemCallbacksJsCommonTest.testSystemCallbacks
 * @see com.github.panpf.sketch.core.ios.test.util.SystemCallbacksIosTest.testSystemCallbacks
 */
internal expect fun SystemCallbacks(sketch: Sketch): SystemCallbacks

/**
 * Monitor network connection and system status
 *
 * @see com.github.panpf.sketch.core.android.test.util.SystemCallbacksAndroidTest.testAndroidSystemCallbacks
 * @see com.github.panpf.sketch.core.desktop.test.util.SystemCallbacksDesktopTest.testDesktopSystemCallbacks
 * @see com.github.panpf.sketch.core.jscommon.test.util.SystemCallbacksJsCommonTest.testJsSystemCallbacks
 * @see com.github.panpf.sketch.core.ios.test.util.SystemCallbacksIosTest.testIosSystemCallbacks
 */
interface SystemCallbacks {

    /**
     * Whether the system is shut down
     */
    val isShutdown: Boolean

    /**
     * Whether the cellular network is connected
     */
    val isCellularNetworkConnected: Boolean

    /**
     * Register system status monitoring
     */
    fun register()

    /**
     * Unregister system status monitoring
     */
    fun shutdown()
}