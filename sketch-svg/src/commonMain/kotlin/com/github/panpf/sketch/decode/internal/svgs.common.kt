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

package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.indexOf
import com.github.panpf.sketch.util.rangeEquals
import okio.ByteString.Companion.encodeUtf8

private val SVG_TAG = "<svg ".encodeUtf8().toByteArray()
private val LEFT_ANGLE_BRACKET = "<".encodeUtf8().toByteArray()

/**
 * Check if the data is an SVG image
 *
 * @see com.github.panpf.sketch.svg.common.test.decode.internal.SvgsTest.testIsSvg
 */
fun ByteArray.isSvg(): Boolean =
    rangeEquals(0, LEFT_ANGLE_BRACKET) && indexOf(SVG_TAG, 0, 1024) != -1

/**
 * Decode the SVG image info
 *
 * @see com.github.panpf.sketch.svg.android.test.decode.internal.SvgsAndroidTest.testReadSvgImageInfo
 * @see com.github.panpf.sketch.svg.nonandroid.test.decode.internal.SvgsNonAndroidTest.testReadSvgImageInfo
 */
internal expect fun DataSource.readSvgImageInfo(
    useViewBoundsAsIntrinsicSize: Boolean = true,
): ImageInfo

/**
 * Decode the SVG image
 *
 * @see com.github.panpf.sketch.svg.android.test.decode.internal.SvgsAndroidTest.testDecodeSvg
 * @see com.github.panpf.sketch.svg.nonandroid.test.decode.internal.SvgsNonAndroidTest.testDecodeSvg
 */
internal expect fun DataSource.decodeSvg(
    requestContext: RequestContext,
    useViewBoundsAsIntrinsicSize: Boolean = true,
    backgroundColor: Int? = null,
    css: String? = null,
): DecodeResult
