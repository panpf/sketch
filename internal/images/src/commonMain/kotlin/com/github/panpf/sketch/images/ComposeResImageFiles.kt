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
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.Size
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.readResourceBytes

object ComposeResImageFiles {
    val jpeg: ComposeResImageFile = ComposeResImageFile(
        name = "sample.jpeg",
        size = Size(1291, 1936),
        length = 540456,
        mimeType = "image/jpeg"
    )
    val png: ComposeResImageFile = ComposeResImageFile(
        name = "sample.png",
        size = Size(750, 719),
        length = 254533,
        mimeType = "image/png"
    )
    val webp: ComposeResImageFile = ComposeResImageFile(
        name = "sample.webp",
        size = Size(1080, 1344),
        length = 341720,
        mimeType = "image/webp"
    )
    val bmp: ComposeResImageFile = ComposeResImageFile(
        name = "sample.bmp",
        size = Size(700, 1012),
        length = 2833654,
        mimeType = "image/bmp"
    )
    val svg: ComposeResImageFile = ComposeResImageFile(
        name = "sample.svg",
        size = Size(256, 225),
        length = 6695,
        mimeType = "image/svg+xml"
    )
    val heic: ComposeResImageFile = ComposeResImageFile(
        name = "sample.heic",
        size = Size(750, 931),
        length = 316083,
        mimeType = "image/heif"
    )
    val avif: ComposeResImageFile = ComposeResImageFile(
        name = "sample.avif",
        size = Size(1204, 800),
        length = 83040,
        mimeType = "image/avif"
    )
    val statics = arrayOf(jpeg, png, webp, bmp, svg, heic, avif)

    val animGif: ComposeResImageFile = ComposeResImageFile(
        name = "sample_anim.gif",
        size = Size(480, 480),
        length = 76938,
        mimeType = "image/gif",
        animated = true
    )
    val animWebp: ComposeResImageFile = ComposeResImageFile(
        name = "sample_anim.webp",
        size = Size(480, 270),
        length = 1406600,
        mimeType = "image/webp",
        animated = true
    )
    val animHeif: ComposeResImageFile = ComposeResImageFile(
        name = "sample_anim.heif",
        size = Size(256, 144),
        length = 381368,
        mimeType = "image/heif",
        animated = true
    )
    val anims = arrayOf(animGif, animWebp, animHeif)

    val mp4: ComposeResImageFile = ComposeResImageFile(
        name = "sample.mp4",
        size = Size(500, 250),
        length = 157092,
        mimeType = "video/mp4"
    )
    val videos = arrayOf(mp4)

    val longQMSHT: ComposeResImageFile = ComposeResImageFile(
        name = "sample_long_qmsht.jpg",
        size = Size(30000, 926),
        length = 8063397,
        mimeType = "image/jpeg"
    )
    val longCOMIC: ComposeResImageFile = ComposeResImageFile(
        name = "sample_long_comic.jpg",
        size = Size(690, 12176),
        length = 1437197,
        mimeType = "image/jpeg"
    )
    val longs = arrayOf(longQMSHT, longCOMIC)

    val clockExifFlipHorizontal: ComposeResImageFile = ComposeResImageFile(
        name = "clock_exif_flip_horizontal.jpeg",
        size = Size(1500, 750),
        length = 162053,
        mimeType = "image/jpeg",
        exifOrientation = ExifOrientation.FLIP_HORIZONTAL
    )
    val clockExifFlipVertical: ComposeResImageFile = ComposeResImageFile(
        name = "clock_exif_flip_vertical.jpeg",
        size = Size(1500, 750),
        length = 166791,
        mimeType = "image/jpeg",
        exifOrientation = ExifOrientation.FLIP_VERTICAL
    )
    val clockExifNormal: ComposeResImageFile = ComposeResImageFile(
        name = "clock_exif_normal.jpeg",
        size = Size(1500, 750),
        length = 127173,
        mimeType = "image/jpeg",
        exifOrientation = ExifOrientation.NORMAL
    )
    val clockExifRotate90: ComposeResImageFile = ComposeResImageFile(
        name = "clock_exif_rotate_90.jpeg",
        size = Size(1500, 750),
        length = 162772,
        mimeType = "image/jpeg",
        exifOrientation = ExifOrientation.ROTATE_90
    )
    val clockExifRotate180: ComposeResImageFile = ComposeResImageFile(
        name = "clock_exif_rotate_180.jpeg",
        size = Size(1500, 750),
        length = 190573,
        mimeType = "image/jpeg",
        exifOrientation = ExifOrientation.ROTATE_180
    )
    val clockExifRotate270: ComposeResImageFile = ComposeResImageFile(
        name = "clock_exif_rotate_270.jpeg",
        size = Size(1500, 750),
        length = 166609,
        mimeType = "image/jpeg",
        exifOrientation = ExifOrientation.ROTATE_270
    )
    val clockExifTranspose: ComposeResImageFile = ComposeResImageFile(
        name = "clock_exif_transpose.jpeg",
        size = Size(1500, 750),
        length = 127270,
        mimeType = "image/jpeg",
        exifOrientation = ExifOrientation.TRANSPOSE
    )
    val clockExifTransverse: ComposeResImageFile = ComposeResImageFile(
        name = "clock_exif_transverse.jpeg",
        size = Size(1500, 750),
        length = 191257,
        mimeType = "image/jpeg",
        exifOrientation = ExifOrientation.TRANSVERSE
    )
    val clockExifUndefined: ComposeResImageFile = ComposeResImageFile(
        name = "clock_exif_undefined.jpeg",
        size = Size(1500, 750),
        length = 93908,
        mimeType = "image/jpeg",
        exifOrientation = ExifOrientation.NORMAL
    )
    val clockExifs = arrayOf(
        clockExifFlipHorizontal,
        clockExifFlipVertical,
        clockExifNormal,
        clockExifRotate90,
        clockExifRotate180,
        clockExifRotate270,
        clockExifTranspose,
        clockExifTransverse,
        clockExifUndefined,
    )

    val clockHor: ComposeResImageFile = ComposeResImageFile(
        name = "clock_hor.jpeg",
        size = Size(1500, 750),
        length = 93908,
        mimeType = "image/jpeg"
    )
    val clockVer: ComposeResImageFile = ComposeResImageFile(
        name = "clock_ver.jpeg",
        size = Size(750, 1500),
        length = 94237,
        mimeType = "image/jpeg"
    )

    val number0: ComposeResImageFile = ComposeResImageFile(
        name = "number_0.png",
        size = Size(698, 776),
        length = 23255,
        mimeType = "image/png"
    )
    val number1: ComposeResImageFile = ComposeResImageFile(
        name = "number_1.png",
        size = Size(698, 776),
        length = 15205,
        mimeType = "image/png"
    )
    val number2: ComposeResImageFile = ComposeResImageFile(
        name = "number_2.png",
        size = Size(698, 776),
        length = 18153,
        mimeType = "image/png"
    )
    val number3: ComposeResImageFile = ComposeResImageFile(
        name = "number_3.png",
        size = Size(698, 776),
        length = 18057,
        mimeType = "image/png"
    )
    val number4: ComposeResImageFile = ComposeResImageFile(
        name = "number_4.png",
        size = Size(698, 776),
        length = 18832,
        mimeType = "image/png"
    )
    val number5: ComposeResImageFile = ComposeResImageFile(
        name = "number_5.png",
        size = Size(698, 776),
        length = 17812,
        mimeType = "image/png"
    )
    val number6: ComposeResImageFile = ComposeResImageFile(
        name = "number_6.png",
        size = Size(698, 776),
        length = 20826,
        mimeType = "image/png"
    )
    val number7: ComposeResImageFile = ComposeResImageFile(
        name = "number_7.png",
        size = Size(698, 776),
        length = 15080,
        mimeType = "image/png"
    )
    val number8: ComposeResImageFile = ComposeResImageFile(
        name = "number_8.png",
        size = Size(698, 776),
        length = 24684,
        mimeType = "image/png"
    )
    val number9: ComposeResImageFile = ComposeResImageFile(
        name = "number_9.png",
        size = Size(698, 776),
        length = 21263,
        mimeType = "image/png"
    )
    val numbers =
        arrayOf(number1, number2, number3, number4, number5, number6, number7, number8, number9)

    val numbersGif: ComposeResImageFile = ComposeResImageFile(
        name = "numbers.gif",
        size = Size(698, 776),
        length = 85394,
        mimeType = "image/gif",
        animated = true
    )

    val values: Array<ComposeResImageFile> = statics
        .plus(anims)
        .plus(videos)
        .plus(longs)
        .plus(clockExifs)
        .plus(arrayOf(clockHor, clockVer))
        .plus(numbers)
        .plus(numbersGif)

    val singleFrameGif: ComposeResImageFile = ComposeResImageFile(
        name = "single_frame.gif",
        size = Size(500, 667),
        length = 204684,
        mimeType = "image/gif",
        animated = true
    )
}

class ComposeResImageFile(
    override val name: String,
    override val size: Size,
    override val length: Long,
    override val mimeType: String,
    override val animated: Boolean = false,
    override val exifOrientation: Int = ExifOrientation.UNDEFINED,
) : ImageFile {

    override val uri = newComposeResourceUri(Res.getUri("files/$name"))

    override val imageInfo = ImageInfo(size, mimeType)

    @OptIn(InternalResourceApi::class)
    override suspend fun toDataSource(context: PlatformContext): DataSource {
        val uri = Res.getUri("files/$name")
        val index = uri.indexOf("composeResources/")
        require(index != -1) {
            "The uri of the resource is invalid: $uri"
        }
        val path = uri.substring(index)
        println("ComposeResImageFile.toDataSource: path=$path")
        val bytes = readResourceBytes(path)
        return ByteArrayDataSource(data = bytes, dataFrom = LOCAL)
    }

    override fun toString(): String =
        "ComposeResImageFile(name='$name', size=$size, mimeType='$mimeType', animated=$animated, exifOrientation=$exifOrientation)"
}

private fun newComposeResourceUri(resourcePath: String): String {
    if (resourcePath.startsWith("composeResources/")) {
        return "file:///compose_resource/$resourcePath"
    }

    val index = resourcePath.indexOf("/composeResources/")
    if (index != -1) {
        val realResourcePath = resourcePath.substring(index + 1)
        return "file:///compose_resource/$realResourcePath"
    }

    throw IllegalArgumentException("Unsupported compose resource path: $resourcePath")
}