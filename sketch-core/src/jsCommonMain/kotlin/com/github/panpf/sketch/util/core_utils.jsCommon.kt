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
actual fun requiredWorkThread() {

}