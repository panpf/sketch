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

package com.github.panpf.sketch.images

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.source.DataSource

actual fun resourceNameToUri(name: String): String {
    // The images in images have not been compiled,
    // so there is no need to add 'composeResources/com.github.panpf.sketch.sample.resources/' to the path.
    return "file:///compose_resource/files/$name"
}

actual fun ResourceImageFile.toDataSource(context: PlatformContext): DataSource {
    throw UnsupportedOperationException("No implementation for js platform")
}