/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * Copyright 2023 Coil Contributors
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

package com.github.panpf.sketch.compose.internal.util

/*
 * There are the same functions in other modules, so the package name must remain unique, otherwise duplicate definition errors will occur in the js environment.
 */

/**
 * Convert to the type specified by the generic, if this is null or cannot be converted return null
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsCommonTest.testAnyAsOrNull
 */
internal inline fun <reified R> Any?.asOrNull(): R? {
    return if (this != null && this is R) this else null
}

/**
 * Returns a string representation of this Int value in the specified radix.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.util.ComposeCoreUtilsCommonTest.testToHexString
 */
internal fun Any.toHexString(): String = this.hashCode().toString(16)