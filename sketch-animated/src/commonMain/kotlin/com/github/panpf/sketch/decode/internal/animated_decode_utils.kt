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

import com.github.panpf.sketch.util.rangeEquals
import okio.ByteString.Companion.encodeUtf8
import kotlin.experimental.and

// https://developers.google.com/speed/webp/docs/riff_container
private val WEBP_HEADER_RIFF = "RIFF".encodeUtf8().toByteArray()
private val WEBP_HEADER_WEBP = "WEBP".encodeUtf8().toByteArray()
private val WEBP_HEADER_VP8X = "VP8X".encodeUtf8().toByteArray()
private val WEBP_HEADER_ANIM = "ANIM".encodeUtf8().toByteArray()

// https://nokiatech.github.io/heif/technical.html
private val HEIF_HEADER_FTYP = "ftyp".encodeUtf8().toByteArray()
private val HEIF_HEADER_MSF1 = "msf1".encodeUtf8().toByteArray()
private val HEIF_HEADER_HEVC = "hevc".encodeUtf8().toByteArray()
private val HEIF_HEADER_HEVX = "hevx".encodeUtf8().toByteArray()

// https://www.matthewflickinger.com/lab/whatsinagif/bits_and_bytes.asp
private val GIF_HEADER_87A = "GIF87a".encodeUtf8().toByteArray()
private val GIF_HEADER_89A = "GIF89a".encodeUtf8().toByteArray()

/**
 * Return 'true' if the [ByteArray] contains a WebP image.
 */
fun ByteArray.isWebP(): Boolean =
    rangeEquals(0, WEBP_HEADER_RIFF) && rangeEquals(8, WEBP_HEADER_WEBP)

/**
 * Return 'true' if the [ByteArray] contains an animated WebP image.
 */
fun ByteArray.isAnimatedWebP(): Boolean = isWebP()
        && rangeEquals(12, WEBP_HEADER_VP8X)
        && (get(16) and 0b00000010) > 0
        // Some webp images do not comply with standard protocols, obviously not GIFs but have GIF markup, here to do a fault tolerance
        // The VP8X block is fixed at 9 bytes, plus the first 16 bytes, for a total of 25 bytes, so an anim block can only start at 25
        && containsRiffAnimChunk(25)

/**
 * Return 'true' if the [ByteArray] contains an HEIF image. The [ByteArray] is not consumed.
 */
fun ByteArray.isHeif(): Boolean = rangeEquals(4, HEIF_HEADER_FTYP)

/**
 * Return 'true' if the [ByteArray] contains an animated HEIF image sequence.
 */
fun ByteArray.isAnimatedHeif(): Boolean = isHeif()
        && (rangeEquals(8, HEIF_HEADER_MSF1)
        || rangeEquals(8, HEIF_HEADER_HEVC)
        || rangeEquals(8, HEIF_HEADER_HEVX))

/**
 * Return 'true' if the [ByteArray] contains a GIF image.
 */
fun ByteArray.isGif(): Boolean =
    rangeEquals(0, GIF_HEADER_89A) || rangeEquals(0, GIF_HEADER_87A)

fun ByteArray.containsRiffAnimChunk(offset: Int = 0): Boolean {
    (offset until size - WEBP_HEADER_ANIM.size).forEach {
        if (rangeEquals(it, WEBP_HEADER_ANIM)) {
            return true
        }
    }
    return false
}