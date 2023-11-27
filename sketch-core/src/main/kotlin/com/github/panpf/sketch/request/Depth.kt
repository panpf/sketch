/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.request

/**
 * The processing depth of the request.
 */
enum class Depth {
    /**
     * Allows loading images from memory, local, and network. Works with [DisplayRequest], [LoadRequest], [DownloadRequest]
     */
    NETWORK,

    /**
     * Load images only from memory or local. Only used for [DisplayRequest] and [LoadRequest]
     */
    LOCAL,

    /**
     * Load images only from memory. Only used for [DisplayRequest]
     */
    MEMORY;
}