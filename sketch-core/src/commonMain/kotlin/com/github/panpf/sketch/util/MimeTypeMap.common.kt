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

package com.github.panpf.sketch.util

/**
 * A map of file extensions to MIME types.
 *
 * @see com.github.panpf.sketch.core.android.test.util.MimeTypeMapAndroidTest.testExtensionToMimeType
 * @see com.github.panpf.sketch.core.nonandroid.test.util.MimeTypeMapNonAndroidTest.testExtensionToMimeType
 */
internal expect fun extensionToMimeType(extension: String): String?

/**
 * A map of MIME types to file extensions.
 *
 * @see com.github.panpf.sketch.core.android.test.util.MimeTypeMapAndroidTest.testMimeTypeToExtension
 * @see com.github.panpf.sketch.core.nonandroid.test.util.MimeTypeMapNonAndroidTest.testMimeTypeToExtension
 */
internal expect fun mimeTypeToExtension(mimeType: String): String?

/**
 * A map of file extensions to MIME types.
 *
 * @see com.github.panpf.sketch.core.common.test.util.MimeTypeMapTest
 */
object MimeTypeMap {

    fun getExtensionFromUrl(url: String): String? {
        if (url.isBlank()) return null
        return url
            .substringBeforeLast('#') // Strip the fragment.
            .substringBeforeLast('?') // Strip the query.
            .substringAfterLast('/') // Get the last path segment.
            .substringAfterLast('.', missingDelimiterValue = "") // Get the file extension.
            .trim()
            .takeIf { it.isNotEmpty() }
    }

    fun getMimeTypeFromUrl(url: String): String? {
        val extension = getExtensionFromUrl(url) ?: return null
        return getMimeTypeFromExtension(extension)
    }

    fun getMimeTypeFromExtension(extension: String): String? {
        val lowerExtension = extension.lowercase()
        return extensionToMimeType(lowerExtension) ?: mimeTypeData[lowerExtension]
    }

    fun getExtensionFromMimeType(mimeType: String): String? {
        val lowerMimeType = mimeType.lowercase()
        return mimeTypeToExtension(lowerMimeType)
            ?: mimeTypeData.entries.find { it.value == lowerMimeType }?.key
    }

    // https://mimetype.io/all-types
    private val mimeTypeData = buildMap {
        put("arw", "image/x-sony-arw")
        put("avif", "image/avif")
        put("avifs", "image/avif")
        put("bmp", "image/bmp")
        put("btif", "image/prs.btif")
        put("cgm", "image/cgm")
        put("cmx", "image/x-cmx")
        put("cr2", "image/x-canon-cr2")
        put("crw", "image/x-canon-crw")
        put("dcr", "image/x-kodak-dcr")
        put("djv", "image/vnd.djvu")
        put("djvu", "image/vnd.djvu")
        put("dng", "image/x-adobe-dng")
        put("dwg", "image/vnd.dwg")
        put("dxf", "image/vnd.dxf")
        put("erf", "image/x-epson-erf")
        put("fbs", "image/vnd.fastbidsheet")
        put("fh", "image/x-freehand")
        put("fh4", "image/x-freehand")
        put("fh5", "image/x-freehand")
        put("fh7", "image/x-freehand")
        put("fhc", "image/x-freehand")
        put("fpx", "image/vnd.fpx")
        put("fst", "image/vnd.fst")
        put("g3", "image/g3fax")
        put("gif", "image/gif")
        put("heic", "image/heic")
        put("heif", "image/heic")
        put("icns", "image/x-icns")
        put("ico", "image/x-icon")
        put("ief", "image/ief")
        put("jfi", "image/pjpeg")
        put("jfif", "image/jpeg")
//        put("jfif", "image/pjpeg")
        put("jfif-tbnl", "image/jpeg")
//        put("jfif-tbnl", "image/pjpeg")
        put("jif", "image/jpeg")
//        put("jif", "image/pjpeg")
        put("jpe", "image/jpeg")
//        put("jpe", "image/pjpeg")
        put("jpeg", "image/jpeg")
//        put("jpeg", "image/pjpeg")
        put("jpg", "image/jpeg")
//        put("jpg", "image/pjpeg")
        put("k25", "image/x-kodak-k25")
        put("kdc", "image/x-kodak-kdc")
        put("mdi", "image/vnd.ms-modi")
        put("mmr", "image/vnd.fujixerox.edmics-mmr")
        put("mrw", "image/x-minolta-mrw")
        put("nef", "image/x-nikon-nef")
        put("npx", "image/vnd.net-fpx")
        put("orf", "image/x-olympus-orf")
        put("pbm", "image/x-portable-bitmap")
        put("pct", "image/x-pict")
        put("pcx", "image/x-pcx")
        put("pef", "image/x-pentax-pef")
        put("pgm", "image/x-portable-graymap")
        put("pic", "image/x-pict")
        put("pjpg", "image/pjpeg")
//        put("pjpg", "image/jpeg")
        put("png", "image/png")
        put("pnm", "image/x-portable-anymap")
        put("ppm", "image/x-portable-pixmap")
        put("psd", "image/vnd.adobe.photoshop")
        put("ptx", "image/x-pentax-pef")
        put("raf", "image/x-fuji-raf")
        put("ras", "image/x-cmu-raster")
        put("raw", "image/x-panasonic-raw")
        put("rgb", "image/x-rgb")
        put("rlc", "image/vnd.fujixerox.edmics-rlc")
        put("rw2", "image/x-panasonic-raw")
        put("rwl", "image/x-panasonic-raw")
        put("sr2", "image/x-sony-sr2")
        put("srf", "image/x-sony-srf")
        put("svg", "image/svg+xml")
        put("svgz", "image/svg+xml")
        put("tif", "image/tiff")
        put("tiff", "image/tiff")
        put("wbmp", "image/vnd.wap.wbmp")
        put("webp", "image/webp")
        put("x3f", "image/x-sigma-x3f")
        put("xbm", "image/x-xbitmap")
        put("xif", "image/vnd.xiff")
        put("xpm", "image/x-xpixmap")
        put("xwd", "image/x-xwindowdump")

        put("3g2", "video/3gpp2")
        put("3gp", "video/3gpp")
        put("asf", "video/x-ms-asf")
        put("asx", "video/x-ms-asf")
        put("avi", "video/x-msvideo")
        put("f4v", "video/x-f4v")
        put("fli", "video/x-fli")
        put("flv", "video/x-flv")
        put("fvt", "video/vnd.fvt")
        put("h261", "video/h261")
        put("h263", "video/h263")
        put("h264", "video/h264")
        put("jpgm", "video/jpm")
        put("jpgv", "video/jpeg")
        put("jpm", "video/jpm")
        put("m1v", "video/mpeg")
        put("m2v", "video/mpeg")
        put("m4u", "video/vnd.mpegurl")
        put("m4v", "video/x-m4v")
        put("mj2", "video/mj2")
        put("mjp2", "video/mj2")
        put("mkv", "video/x-matroska")
        put("mov", "video/quicktime")
        put("movie", "video/x-sgi-movie")
        put("mp4", "video/mp4")
        put("mp4v", "video/mp4")
        put("mpa", "video/mpeg")
        put("mpe", "video/mpeg")
        put("mpeg", "video/mpeg")
        put("mpg", "video/mpeg")
        put("mpg4", "video/mp4")
        put("mxu", "video/vnd.mpegurl")
        put("ogv", "video/ogg")
        put("pyv", "video/vnd.ms-playready.media.pyv")
        put("qt", "video/quicktime")
        put("ts", "video/mp2t")
        put("viv", "video/vnd.vivo")
        put("webm", "video/webm")
        put("wm", "video/x-ms-wm")
        put("wmv", "video/x-ms-wmv")
        put("wmx", "video/x-ms-wmx")
        put("wvx", "video/x-ms-wvx")
    }
}