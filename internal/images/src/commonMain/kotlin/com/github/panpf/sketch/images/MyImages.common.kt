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

import com.github.panpf.sketch.decode.ExifOrientation
import com.github.panpf.sketch.util.Size

expect fun nameToUri(name: String): String

object MyImages {

    val bmp: MyImage = MyResourceImage("sample.bmp", "BMP", Size(700, 1012))
    val heic: MyImage = MyResourceImage("sample.heic", "HEIC", Size(750, 931))
    val jpeg: MyImage = MyResourceImage("sample.jpeg", "JPEG", Size(1291, 1936))
    val png: MyImage = MyResourceImage("sample.png", "PNG", Size(750, 719))
    val svg: MyImage = MyResourceImage("sample.svg", "SVG", Size(256, 225))
    val webp: MyImage = MyResourceImage("sample.webp", "WEBP", Size(1080, 1344))
    val statics = arrayOf(jpeg, png, webp, bmp, svg, heic)

    val animGif: MyImage = MyResourceImage("sample_anim.gif", "GIF", Size(480, 480))
    val animHeif: MyImage = MyResourceImage("sample_anim.heif", "HEIF_ANIM", Size(256, 144))
    val animWebp: MyImage = MyResourceImage("sample_anim.webp", "WEBP_ANIM", Size(480, 270))
    val anims = arrayOf(animGif, animWebp, animHeif)

    val mp4: MyImage = MyResourceImage("sample.mp4", "MP4", Size(500, 250))
    val videos = arrayOf(mp4)

    val longQMSHT: MyImage = MyResourceImage("sample_long_qmsht.jpg", "QMSHT", Size(30000, 926))

    val clockExifFlipHorizontal: MyImage = MyResourceImage(
        "clock_exif_flip_horizontal.jpeg",
        "FLIP_HOR",
        Size(1500, 750),
        ExifOrientation.FLIP_HORIZONTAL
    )
    val clockExifFlipVertical: MyImage = MyResourceImage(
        "clock_exif_flip_vertical.jpeg",
        "FLIP_VER",
        Size(1500, 750),
        ExifOrientation.FLIP_VERTICAL
    )
    val clockExifNormal: MyImage = MyResourceImage(
        "clock_exif_normal.jpeg",
        "NORMAL",
        Size(1500, 750),
        ExifOrientation.NORMAL
    )
    val clockExifRotate90: MyImage = MyResourceImage(
        "clock_exif_rotate_90.jpeg",
        "ROTATE_90",
        Size(750, 1500),
        ExifOrientation.ROTATE_90
    )
    val clockExifRotate180: MyImage = MyResourceImage(
        "clock_exif_rotate_180.jpeg",
        "ROTATE_180",
        Size(1500, 750),
        ExifOrientation.ROTATE_180
    )
    val clockExifRotate270: MyImage = MyResourceImage(
        "clock_exif_rotate_270.jpeg",
        "ROTATE_270",
        Size(750, 1500),
        ExifOrientation.ROTATE_270
    )
    val clockExifTranspose: MyImage = MyResourceImage(
        "clock_exif_transpose.jpeg",
        "TRANSPOSE",
        Size(750, 1500),
        ExifOrientation.TRANSPOSE
    )
    val clockExifTransverse: MyImage = MyResourceImage(
        "clock_exif_transverse.jpeg",
        "TRANSVERSE",
        Size(750, 1500),
        ExifOrientation.TRANSVERSE
    )
    val clockExifUndefined: MyImage = MyResourceImage(
        "clock_exif_undefined.jpeg",
        "UNDEFINED",
        Size(1500, 750),
        ExifOrientation.UNDEFINED
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

    val number1: MyImage = MyResourceImage("number_1.png", "NUMBER_1", Size(698, 776))
    val number2: MyImage = MyResourceImage("number_2.png", "NUMBER_2", Size(698, 776))
    val number3: MyImage = MyResourceImage("number_3.png", "NUMBER_3", Size(698, 776))
    val number4: MyImage = MyResourceImage("number_4.png", "NUMBER_4", Size(698, 776))
    val number5: MyImage = MyResourceImage("number_5.png", "NUMBER_5", Size(698, 776))
    val number6: MyImage = MyResourceImage("number_6.png", "NUMBER_6", Size(698, 776))
    val number7: MyImage = MyResourceImage("number_7.png", "NUMBER_7", Size(698, 776))
    val number8: MyImage = MyResourceImage("number_8.png", "NUMBER_8", Size(698, 776))
    val number9: MyImage = MyResourceImage("number_9.png", "NUMBER_9", Size(698, 776))
    val numbers =
        arrayOf(number1, number2, number3, number4, number5, number6, number7, number8, number9)

    val clockHor: MyImage = MyResourceImage("clock_hor.jpeg", "CLOCK_HOR", Size(1500, 750))
    val clockVer: MyImage = MyResourceImage("clock_ver.jpeg", "CLOCK_VER", Size(750, 1500))

    const val HTTP = "http://img.panpengfei.com/sample_antelope.jpg"

    const val HTTPS =
        "https://images.unsplash.com/photo-1431440869543-efaf3388c585?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&s=8b00971a3e4a84fb43403797126d1991%22"
    const val BASE64_IMAGE =
        "data:image/jpeg;base64,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"
}