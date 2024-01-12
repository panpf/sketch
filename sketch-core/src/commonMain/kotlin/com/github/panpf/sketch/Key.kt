/*
 * Copyright (C) 2023 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch

interface Key {

    /**
     * The unique key. Used to build request key or cache key
     *
     * Key should contain the simple name and parameters of the current object,
     * and should satisfy the requirement that in multiple instances,
     * when the parameters are the same, their keys are also the same,
     * and when the parameters are different, their keys are also different.
     *
     * @see com.github.panpf.sketch.request.internal.ImageRequestKeyBuilder
     */
    val key: String

    companion object {
        const val INVALID_KEY = "INVALID_KEY"
    }
}