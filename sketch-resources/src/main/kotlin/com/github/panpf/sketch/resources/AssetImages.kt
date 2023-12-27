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
package com.github.panpf.sketch.resources

import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.util.Size

object AssetImages {

    val bmp = Image("sample.bmp", "BMP", Size(700, 1012))
    val heic = Image("sample.heic", "HEIC", Size(750, 931))
    val jpeg = Image("sample.jpeg", "JPEG", Size(1291, 1936))
    val png = Image("sample.png", "PNG", Size(750, 719))
    val svg = Image("sample.svg", "SVG", Size(842, 595))
    val webp = Image("sample.webp", "WEBP", Size(1080, 1344))
    val statics = arrayOf(jpeg, png, webp, bmp, svg, heic)

    val animGif = Image("sample_anim.gif", "GIF", Size(480, 480))
    val animHeif = Image("sample_anim.heif", "HEIF_ANIM", Size(256, 144))
    val animWebp = Image("sample_anim.webp", "WEBP_ANIM", Size(480, 270))
    val anims = arrayOf(animGif, animWebp, animHeif)

    val mp4 = Image("sample.mp4", "MP4", Size(500, 250))
    val videos = arrayOf(mp4)

    val number1 = Image("number_1.png", "NUMBER_1", Size(698, 776))
    val number2 = Image("number_2.png", "NUMBER_2", Size(698, 776))
    val number3 = Image("number_3.png", "NUMBER_3", Size(698, 776))
    val number4 = Image("number_4.png", "NUMBER_4", Size(698, 776))
    val number5 = Image("number_5.png", "NUMBER_5", Size(698, 776))
    val number6 = Image("number_6.png", "NUMBER_6", Size(698, 776))
    val number7 = Image("number_7.png", "NUMBER_7", Size(698, 776))
    val number8 = Image("number_8.png", "NUMBER_8", Size(698, 776))
    val number9 = Image("number_9.png", "NUMBER_9", Size(698, 776))
    val numbers =
        arrayOf(number1, number2, number3, number4, number5, number6, number7, number8, number9)

    val clockHor = Image("clock_hor.jpeg", "CLOCK_HOR", Size(1500, 750))
    val clockVer = Image("clock_ver.jpeg", "CLOCK_VER", Size(750, 1500))

    class Image(val fileName: String, val name: String, val size: Size) {
        val uri: String by lazy { newAssetUri(fileName) }
    }
}