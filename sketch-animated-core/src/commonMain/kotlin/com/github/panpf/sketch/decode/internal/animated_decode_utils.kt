/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * Copyright 2023 Coil Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.util.isAnimatedHeifFile
import com.github.panpf.sketch.util.isAnimatedWebPFile
import com.github.panpf.sketch.util.isGifFile
import com.github.panpf.sketch.util.isHeifFile
import com.github.panpf.sketch.util.isWebPFile

/**
 * Return 'true' if the [ByteArray] contains a WebP image.
 *
 * @see com.github.panpf.sketch.animated.core.common.test.decode.internal.AnimatedDecodeUtilsTest.testIsWebP
 */
@Deprecated("Use isWebPFile instead", ReplaceWith("isWebPFile(this)"))
fun ByteArray.isWebP(): Boolean = isWebPFile(this)

/**
 * Return 'true' if the [ByteArray] contains an animated WebP image.
 *
 * @see com.github.panpf.sketch.animated.core.common.test.decode.internal.AnimatedDecodeUtilsTest.testIsAnimatedWebP
 */
@Deprecated("Use isAnimatedWebPFile instead", ReplaceWith("isAnimatedWebPFile(this)"))
fun ByteArray.isAnimatedWebP(): Boolean = isAnimatedWebPFile(this)

/**
 * Return 'true' if the [ByteArray] contains an HEIF image. The [ByteArray] is not consumed.
 *
 * @see com.github.panpf.sketch.animated.core.common.test.decode.internal.AnimatedDecodeUtilsTest.testIsHeif
 */
@Deprecated("Use isHeifFile instead", ReplaceWith("isHeifFile(this)"))
fun ByteArray.isHeif(): Boolean = isHeifFile(this)

/**
 * Return 'true' if the [ByteArray] contains an animated HEIF image sequence.
 *
 * @see com.github.panpf.sketch.animated.core.common.test.decode.internal.AnimatedDecodeUtilsTest.testIsAnimatedHeif
 */
@Deprecated("Use isAnimatedHeifFile instead", ReplaceWith("isAnimatedHeifFile(this)"))
fun ByteArray.isAnimatedHeif(): Boolean = isAnimatedHeifFile(this)

/**
 * Return 'true' if the [ByteArray] contains a GIF image.
 *
 * @see com.github.panpf.sketch.animated.core.common.test.decode.internal.AnimatedDecodeUtilsTest.testIsGif
 */
@Deprecated("Use isGifFile instead", ReplaceWith("isGifFile(this)"))
fun ByteArray.isGif(): Boolean = isGifFile(this)