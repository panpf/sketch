/*
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

import com.github.panpf.sketch.util.Bytes
import kotlin.experimental.and

// https://developers.google.com/speed/webp/docs/riff_container
private val WEBP_HEADER_RIFF = "RIFF".toByteArray()
private val WEBP_HEADER_WEBP = "WEBP".toByteArray()
private val WEBP_HEADER_VP8X = "VP8X".toByteArray()
private val WEBP_HEADER_ANIM = "ANIM".toByteArray()

// https://nokiatech.github.io/heif/technical.html
private val HEIF_HEADER_FTYP = "ftyp".toByteArray()
private val HEIF_HEADER_MSF1 = "msf1".toByteArray()
private val HEIF_HEADER_HEVC = "hevc".toByteArray()
private val HEIF_HEADER_HEVX = "hevx".toByteArray()

// https://www.matthewflickinger.com/lab/whatsinagif/bits_and_bytes.asp
private val GIF_HEADER_87A = "GIF87a".toByteArray()
private val GIF_HEADER_89A = "GIF89a".toByteArray()

/**
 * Return 'true' if the [Bytes] contains a WebP image.
 */
fun Bytes.isWebP(): Boolean =
    rangeEquals(0, WEBP_HEADER_RIFF) && rangeEquals(8, WEBP_HEADER_WEBP)

/**
 * Return 'true' if the [Bytes] contains an animated WebP image.
 */
fun Bytes.isAnimatedWebP(): Boolean = isWebP()
        && rangeEquals(12, WEBP_HEADER_VP8X)
        && (get(16) and 0b00000010) > 0
        // Some webp images do not comply with standard protocols, obviously not GIFs but have GIF markup, here to do a fault tolerance
        // The VP8X block is fixed at 9 bytes, plus the first 16 bytes, for a total of 25 bytes, so an anim block can only start at 25
        && containsRiffAnimChunk(25)

/**
 * Return 'true' if the [Bytes] contains an HEIF image. The [Bytes] is not consumed.
 */
fun Bytes.isHeif(): Boolean = rangeEquals(4, HEIF_HEADER_FTYP)

/**
 * Return 'true' if the [Bytes] contains an animated HEIF image sequence.
 */
fun Bytes.isAnimatedHeif(): Boolean = isHeif()
        && (rangeEquals(8, HEIF_HEADER_MSF1)
        || rangeEquals(8, HEIF_HEADER_HEVC)
        || rangeEquals(8, HEIF_HEADER_HEVX))

/**
 * Return 'true' if the [Bytes] contains a GIF image.
 */
fun Bytes.isGif(): Boolean =
    rangeEquals(0, GIF_HEADER_89A) || rangeEquals(0, GIF_HEADER_87A)

fun Bytes.containsRiffAnimChunk(offset: Int = 0): Boolean {
    (offset until size - WEBP_HEADER_ANIM.size).forEach {
        if (rangeEquals(it, WEBP_HEADER_ANIM)) {
            return true
        }
    }
    return false
}