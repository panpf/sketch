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

import okio.FileSystem
import okio.Path

/**
 * Download data
 *
 * @see com.github.panpf.sketch.core.common.test.util.DownloadDataTest
 */
sealed interface DownloadData {
    /**
     * Bytes type download results
     */
    class Bytes(val bytes: ByteArray) : DownloadData

    /**
     * File type download results
     */
    class Cache(val fileSystem: FileSystem, val path: Path) : DownloadData
}