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

package com.github.panpf.sketch

import com.github.panpf.sketch.decode.internal.BitmapFactoryDecoder
import com.github.panpf.sketch.decode.internal.DrawableDecoder
import com.github.panpf.sketch.fetch.AssetUriFetcher
import com.github.panpf.sketch.fetch.ContentUriFetcher
import com.github.panpf.sketch.fetch.ResourceUriFetcher

/**
 * Android platform related components
 *
 * @see com.github.panpf.sketch.core.android.test.SketchAndroidTest.testPlatformComponents
 */
internal actual fun platformComponents(context: PlatformContext): ComponentRegistry {
    return ComponentRegistry {
        add(ContentUriFetcher.Factory())
        add(ResourceUriFetcher.Factory())
        add(AssetUriFetcher.Factory())

        add(DrawableDecoder.Factory())
        add(BitmapFactoryDecoder.Factory())
    }
}