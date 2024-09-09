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

import android.webkit.MimeTypeMap

/**
 * A map of file extensions to MIME types.
 *
 * @see com.github.panpf.sketch.core.android.test.util.MimeTypeMapAndroidTest.testPlatformExtensionToMimeType
 */
internal actual fun platformExtensionToMimeType(extension: String): String? {
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
}

/**
 * A map of MIME types to file extensions.
 *
 * @see com.github.panpf.sketch.core.android.test.util.MimeTypeMapAndroidTest.testPlatformMimeTypeToExtension
 */
internal actual fun platformMimeTypeToExtension(mimeType: String): String? {
    return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
}