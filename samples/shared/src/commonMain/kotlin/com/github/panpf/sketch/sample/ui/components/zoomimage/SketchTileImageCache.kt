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

package com.github.panpf.zoomimage.sketch

import com.github.panpf.sketch.Sketch
import com.github.panpf.zoomimage.subsampling.ImageInfo
import com.github.panpf.zoomimage.subsampling.TileImage
import com.github.panpf.zoomimage.subsampling.TileImageCache

/**
 * Implement [TileImageCache] based on Sketch
 *
 * @see com.github.panpf.zoomimage.core.sketch4.android.test.SketchTileImageCacheTest
 * @see com.github.panpf.zoomimage.core.sketch4.nonandroid.test.SketchTileImageCacheTest
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class SketchTileImageCache(sketch: Sketch) : TileImageCache {

    override fun get(key: String): TileImage?

    override fun put(
        key: String,
        tileImage: TileImage,
        imageUrl: String,
        imageInfo: ImageInfo,
    ): TileImage?
}