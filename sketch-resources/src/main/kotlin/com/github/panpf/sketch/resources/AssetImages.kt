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

object AssetImages {

    val jpeg = "asset://sample.jpeg"
    val png = "asset://sample.png"
    val webp = "asset://sample.webp"
    val bmp = "asset://sample.bmp"
    val svg = "asset://sample.svg"
    val heic = "asset://sample.heic"
    val animGif = "asset://sample_anim.gif"
    val animWebp = "asset://sample_anim.webp"
    val animHeif = "asset://sample_anim.heif"
    val mp4 = "asset://sample.mp4"
    val number1 = "asset://number_1.png"
    val number2 = "asset://number_2.png"
    val number3 = "asset://number_3.png"
    val number4 = "asset://number_4.png"
    val number5 = "asset://number_5.png"
    val number6 = "asset://number_6.png"
    val number7 = "asset://number_7.png"
    val number8 = "asset://number_8.png"
    val number9 = "asset://number_9.png"

    val statics = arrayOf(jpeg, png, webp, bmp, svg, heic)
    val anims = arrayOf(animGif, animWebp, animHeif)
    val videos = arrayOf(mp4)
    val numbers =
        arrayOf(number1, number2, number3, number4, number5, number6, number7, number8, number9)

    // todo add clock_hor.jpeg
    // todo add clock_ver.jpeg
    // todo Add Item
    class Item (val fileName: String) {
        val imageUri: String by lazy { "asset://$fileName" }
    }
}