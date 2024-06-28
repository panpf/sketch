/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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

import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.util.Size

object ResourceImages {

    val bmp: ResourceImageFile = ResourceImageFile("sample.bmp", "BMP", Size(700, 1012))
    val heic: ResourceImageFile = ResourceImageFile("sample.heic", "HEIC", Size(750, 931))
    val jpeg: ResourceImageFile = ResourceImageFile("sample.jpeg", "JPEG", Size(1291, 1936))
    val png: ResourceImageFile = ResourceImageFile("sample.png", "PNG", Size(750, 719))
    val svg: ResourceImageFile = ResourceImageFile("sample.svg", "SVG", Size(256, 225))
    val webp: ResourceImageFile = ResourceImageFile("sample.webp", "WEBP", Size(1080, 1344))
    val statics = arrayOf(jpeg, png, webp, bmp, svg, heic)

    val animGif: ResourceImageFile = ResourceImageFile("sample_anim.gif", "GIF", Size(480, 480))
    val animHeif: ResourceImageFile = ResourceImageFile("sample_anim.heif", "HEIF_ANIM", Size(256, 144))
    val animWebp: ResourceImageFile = ResourceImageFile("sample_anim.webp", "WEBP_ANIM", Size(480, 270))
    val anims = arrayOf(animGif, animWebp, animHeif)

    val mp4: ResourceImageFile = ResourceImageFile("sample.mp4", "MP4", Size(500, 250))
    val videos = arrayOf(mp4)

    val longQMSHT: ResourceImageFile = ResourceImageFile("sample_long_qmsht.jpg", "QMSHT", Size(30000, 926))

    val clockExifFlipHorizontal: ResourceImageFile = ResourceImageFile(
        "clock_exif_flip_horizontal.jpeg",
        "FLIP_HOR",
        Size(1500, 750),
        ExifOrientationHelper.FLIP_HORIZONTAL
    )
    val clockExifFlipVertical: ResourceImageFile = ResourceImageFile(
        "clock_exif_flip_vertical.jpeg",
        "FLIP_VER",
        Size(1500, 750),
        ExifOrientationHelper.FLIP_VERTICAL
    )
    val clockExifNormal: ResourceImageFile = ResourceImageFile(
        "clock_exif_normal.jpeg",
        "NORMAL",
        Size(1500, 750),
        ExifOrientationHelper.NORMAL
    )
    val clockExifRotate90: ResourceImageFile = ResourceImageFile(
        "clock_exif_rotate_90.jpeg",
        "ROTATE_90",
        Size(750, 1500),
        ExifOrientationHelper.ROTATE_90
    )
    val clockExifRotate180: ResourceImageFile = ResourceImageFile(
        "clock_exif_rotate_180.jpeg",
        "ROTATE_180",
        Size(1500, 750),
        ExifOrientationHelper.ROTATE_180
    )
    val clockExifRotate270: ResourceImageFile = ResourceImageFile(
        "clock_exif_rotate_270.jpeg",
        "ROTATE_270",
        Size(750, 1500),
        ExifOrientationHelper.ROTATE_270
    )
    val clockExifTranspose: ResourceImageFile = ResourceImageFile(
        "clock_exif_transpose.jpeg",
        "TRANSPOSE",
        Size(750, 1500),
        ExifOrientationHelper.TRANSPOSE
    )
    val clockExifTransverse: ResourceImageFile = ResourceImageFile(
        "clock_exif_transverse.jpeg",
        "TRANSVERSE",
        Size(750, 1500),
        ExifOrientationHelper.TRANSVERSE
    )
    val clockExifUndefined: ResourceImageFile = ResourceImageFile(
        "clock_exif_undefined.jpeg",
        "UNDEFINED",
        Size(1500, 750),
        ExifOrientationHelper.UNDEFINED
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

    val number1: ResourceImageFile = ResourceImageFile("number_1.png", "NUMBER_1", Size(698, 776))
    val number2: ResourceImageFile = ResourceImageFile("number_2.png", "NUMBER_2", Size(698, 776))
    val number3: ResourceImageFile = ResourceImageFile("number_3.png", "NUMBER_3", Size(698, 776))
    val number4: ResourceImageFile = ResourceImageFile("number_4.png", "NUMBER_4", Size(698, 776))
    val number5: ResourceImageFile = ResourceImageFile("number_5.png", "NUMBER_5", Size(698, 776))
    val number6: ResourceImageFile = ResourceImageFile("number_6.png", "NUMBER_6", Size(698, 776))
    val number7: ResourceImageFile = ResourceImageFile("number_7.png", "NUMBER_7", Size(698, 776))
    val number8: ResourceImageFile = ResourceImageFile("number_8.png", "NUMBER_8", Size(698, 776))
    val number9: ResourceImageFile = ResourceImageFile("number_9.png", "NUMBER_9", Size(698, 776))
    val numbers =
        arrayOf(number1, number2, number3, number4, number5, number6, number7, number8, number9)

    val clockHor: ResourceImageFile = ResourceImageFile("clock_hor.jpeg", "CLOCK_HOR", Size(1500, 750))
    val clockVer: ResourceImageFile = ResourceImageFile("clock_ver.jpeg", "CLOCK_VER", Size(750, 1500))

    val values: Array<ResourceImageFile> = arrayOf(
        bmp, heic, jpeg, png, svg, webp, animGif, animHeif, animWebp, mp4, longQMSHT,
        clockExifFlipHorizontal, clockExifFlipVertical, clockExifNormal, clockExifRotate90,
        clockExifRotate180, clockExifRotate270, clockExifTranspose, clockExifTransverse,
        clockExifUndefined, number1, number2, number3, number4, number5, number6, number7, number8,
        number9, clockHor, clockVer
    )
}