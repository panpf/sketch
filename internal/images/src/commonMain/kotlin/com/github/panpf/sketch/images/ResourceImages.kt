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

import com.github.panpf.sketch.util.Size

object ResourceImages {

    val jpeg: ResourceImageFile = ResourceImageFile(
        resourceName = "sample.jpeg",
        name = "JPEG",
        size = Size(1291, 1936),
        mimeType = "image/jpeg"
    )
    val png: ResourceImageFile = ResourceImageFile(
        resourceName = "sample.png",
        name = "PNG",
        size = Size(750, 719),
        mimeType = "image/png"
    )
    val webp: ResourceImageFile = ResourceImageFile(
        resourceName = "sample.webp",
        name = "WEBP",
        size = Size(1080, 1344),
        mimeType = "image/webp"
    )
    val bmp: ResourceImageFile = ResourceImageFile(
        resourceName = "sample.bmp",
        name = "BMP",
        size = Size(700, 1012),
        mimeType = "image/bmp"
    )
    val svg: ResourceImageFile = ResourceImageFile(
        resourceName = "sample.svg",
        name = "SVG",
        size = Size(256, 225),
        mimeType = "image/svg+xml"
    )
    val heic: ResourceImageFile = ResourceImageFile(
        resourceName = "sample.heic",
        name = "HEIC",
        size = Size(750, 931),
        mimeType = "image/heif"
    )
    val avif: ResourceImageFile = ResourceImageFile(
        resourceName = "sample.avif",
        name = "AVIF",
        size = Size(1204, 800),
        mimeType = "image/avif"
    )
    val statics = arrayOf(jpeg, png, webp, bmp, svg, heic, avif)

    val animGif: ResourceImageFile = ResourceImageFile(
        resourceName = "sample_anim.gif",
        name = "GIF",
        size = Size(480, 480),
        mimeType = "image/gif",
        animated = true
    )
    val animWebp: ResourceImageFile = ResourceImageFile(
        resourceName = "sample_anim.webp",
        name = "WEBP_ANIM",
        size = Size(480, 270),
        mimeType = "image/webp",
        animated = true
    )
    val animHeif: ResourceImageFile = ResourceImageFile(
        resourceName = "sample_anim.heif",
        name = "HEIF_ANIM",
        size = Size(256, 144),
        mimeType = "image/heif",
        animated = true
    )
    val anims = arrayOf(animGif, animWebp, animHeif)

    val mp4: ResourceImageFile = ResourceImageFile(
        resourceName = "sample.mp4",
        name = "MP4",
        size = Size(500, 250),
        mimeType = "video/mp4"
    )
    val videos = arrayOf(mp4)

    val longQMSHT: ResourceImageFile = ResourceImageFile(
        resourceName = "sample_long_qmsht.jpg",
        name = "QMSHT",
        size = Size(30000, 926),
        mimeType = "image/jpeg"
    )
    val longCOMIC: ResourceImageFile = ResourceImageFile(
        resourceName = "sample_long_comic.jpg",
        name = "COMIC",
        size = Size(690, 12176),
        mimeType = "image/jpeg"
    )
    val longs = arrayOf(longQMSHT, longCOMIC)

    val clockExifFlipHorizontal: ResourceImageFile = ResourceImageFile(
        resourceName = "clock_exif_flip_horizontal.jpeg",
        name = "FLIP_HOR",
        size = Size(1500, 750),
        mimeType = "image/jpeg",
        exifOrientation = ExifOrientation.FLIP_HORIZONTAL
    )
    val clockExifFlipVertical: ResourceImageFile = ResourceImageFile(
        resourceName = "clock_exif_flip_vertical.jpeg",
        name = "FLIP_VER",
        size = Size(1500, 750),
        mimeType = "image/jpeg",
        exifOrientation = ExifOrientation.FLIP_VERTICAL
    )
    val clockExifNormal: ResourceImageFile = ResourceImageFile(
        resourceName = "clock_exif_normal.jpeg",
        name = "NORMAL",
        size = Size(1500, 750),
        mimeType = "image/jpeg",
        exifOrientation = ExifOrientation.NORMAL
    )
    val clockExifRotate90: ResourceImageFile = ResourceImageFile(
        resourceName = "clock_exif_rotate_90.jpeg",
        name = "ROTATE_90",
        size = Size(1500, 750),
        mimeType = "image/jpeg",
        exifOrientation = ExifOrientation.ROTATE_90
    )
    val clockExifRotate180: ResourceImageFile = ResourceImageFile(
        resourceName = "clock_exif_rotate_180.jpeg",
        name = "ROTATE_180",
        size = Size(1500, 750),
        mimeType = "image/jpeg",
        exifOrientation = ExifOrientation.ROTATE_180
    )
    val clockExifRotate270: ResourceImageFile = ResourceImageFile(
        resourceName = "clock_exif_rotate_270.jpeg",
        name = "ROTATE_270",
        size = Size(1500, 750),
        mimeType = "image/jpeg",
        exifOrientation = ExifOrientation.ROTATE_270
    )
    val clockExifTranspose: ResourceImageFile = ResourceImageFile(
        resourceName = "clock_exif_transpose.jpeg",
        name = "TRANSPOSE",
        size = Size(1500, 750),
        mimeType = "image/jpeg",
        exifOrientation = ExifOrientation.TRANSPOSE
    )
    val clockExifTransverse: ResourceImageFile = ResourceImageFile(
        resourceName = "clock_exif_transverse.jpeg",
        name = "TRANSVERSE",
        size = Size(1500, 750),
        mimeType = "image/jpeg",
        exifOrientation = ExifOrientation.TRANSVERSE
    )
    val clockExifUndefined: ResourceImageFile = ResourceImageFile(
        resourceName = "clock_exif_undefined.jpeg",
        name = "UNDEFINED",
        size = Size(1500, 750),
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

    val clockHor: ResourceImageFile = ResourceImageFile(
        resourceName = "clock_hor.jpeg",
        name = "CLOCK_HOR",
        size = Size(1500, 750),
        mimeType = "image/jpeg"
    )
    val clockVer: ResourceImageFile = ResourceImageFile(
        resourceName = "clock_ver.jpeg",
        name = "CLOCK_VER",
        size = Size(750, 1500),
        mimeType = "image/jpeg"
    )

    val number1: ResourceImageFile = ResourceImageFile(
        resourceName = "number_1.png",
        name = "NUMBER_1",
        size = Size(698, 776),
        mimeType = "image/png"
    )
    val number2: ResourceImageFile = ResourceImageFile(
        resourceName = "number_2.png",
        name = "NUMBER_2",
        size = Size(698, 776),
        mimeType = "image/png"
    )
    val number3: ResourceImageFile = ResourceImageFile(
        resourceName = "number_3.png",
        name = "NUMBER_3",
        size = Size(698, 776),
        mimeType = "image/png"
    )
    val number4: ResourceImageFile = ResourceImageFile(
        resourceName = "number_4.png",
        name = "NUMBER_4",
        size = Size(698, 776),
        mimeType = "image/png"
    )
    val number5: ResourceImageFile = ResourceImageFile(
        resourceName = "number_5.png",
        name = "NUMBER_5",
        size = Size(698, 776),
        mimeType = "image/png"
    )
    val number6: ResourceImageFile = ResourceImageFile(
        resourceName = "number_6.png",
        name = "NUMBER_6",
        size = Size(698, 776),
        mimeType = "image/png"
    )
    val number7: ResourceImageFile = ResourceImageFile(
        resourceName = "number_7.png",
        name = "NUMBER_7",
        size = Size(698, 776),
        mimeType = "image/png"
    )
    val number8: ResourceImageFile = ResourceImageFile(
        resourceName = "number_8.png",
        name = "NUMBER_8",
        size = Size(698, 776),
        mimeType = "image/png"
    )
    val number9: ResourceImageFile = ResourceImageFile(
        resourceName = "number_9.png",
        name = "NUMBER_9",
        size = Size(698, 776),
        mimeType = "image/png"
    )
    val numbers =
        arrayOf(number1, number2, number3, number4, number5, number6, number7, number8, number9)

    val numbersGif: ResourceImageFile = ResourceImageFile(
        resourceName = "numbers.gif",
        name = "NUMBERS",
        size = Size(698, 776),
        mimeType = "image/gif",
        animated = true
    )

    val values: Array<ResourceImageFile> = statics
        .plus(anims)
        .plus(videos)
        .plus(longs)
        .plus(clockExifs)
        .plus(arrayOf(clockHor, clockVer))
        .plus(numbers)
        .plus(numbersGif)
}