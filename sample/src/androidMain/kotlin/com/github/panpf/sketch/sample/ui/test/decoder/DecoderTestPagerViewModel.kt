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
package com.github.panpf.sketch.sample.ui.test.decoder

import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build.VERSION_CODES
import androidx.lifecycle.viewModelScope
import com.github.panpf.sketch.decode.ApkIconDecoder
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.FFmpegVideoFrameDecoder
import com.github.panpf.sketch.decode.GifAnimatedDecoder
import com.github.panpf.sketch.decode.GifDrawableDecoder
import com.github.panpf.sketch.decode.GifMovieDecoder
import com.github.panpf.sketch.decode.HeifAnimatedDecoder
import com.github.panpf.sketch.decode.SvgDecoder
import com.github.panpf.sketch.decode.VideoFrameDecoder
import com.github.panpf.sketch.decode.WebpAnimatedDecoder
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.sample.R.drawable
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DecoderTestViewModel(application1: Application) : LifecycleAndroidViewModel(application1) {

    private val _data = MutableStateFlow<List<DecoderTestItem>>(emptyList())
    val data: StateFlow<List<DecoderTestItem>> = _data

    init {
        load()
    }

    @SuppressLint("NewApi")
    private fun load() {
        viewModelScope.launch {
            _data.value = buildList {
                add(DecoderTestItem(name = "JPEG", imageUri = AssetImages.jpeg.uri, minAPI = null))
                add(DecoderTestItem(name = "PNG", imageUri = AssetImages.png.uri, minAPI = null))
                add(DecoderTestItem(name = "WEBP", imageUri = AssetImages.webp.uri, minAPI = null))
                add(DecoderTestItem(name = "BMP", imageUri = AssetImages.bmp.uri, minAPI = null))
                add(
                    DecoderTestItem(
                        name = "SVG",
                        imageUri = AssetImages.svg.uri,
                        minAPI = null,
                        imageDecoder = SvgDecoder.Factory()
                    )
                )
                add(
                    DecoderTestItem(
                        name = "HEIC",
                        imageUri = AssetImages.heic.uri,
                        minAPI = VERSION_CODES.P
                    )
                )
                add(
                    DecoderTestItem(
                        name = "GIF_KORAL",
                        imageUri = AssetImages.animGif.uri,
                        minAPI = null,
                        imageDecoder = GifDrawableDecoder.Factory()
                    )
                )
                add(
                    DecoderTestItem(
                        name = "GIF_MOVIE",
                        imageUri = AssetImages.animGif.uri,
                        minAPI = VERSION_CODES.KITKAT,
                        imageDecoder = GifMovieDecoder.Factory()
                    )
                )
                add(
                    DecoderTestItem(
                        name = "GIF_ANIMATED",
                        imageUri = AssetImages.animGif.uri,
                        minAPI = VERSION_CODES.P,
                        imageDecoder = GifAnimatedDecoder.Factory()
                    )
                )
                add(
                    DecoderTestItem(
                        name = "WEBP_ANIMATED",
                        imageUri = AssetImages.animWebp.uri,
                        minAPI = VERSION_CODES.P,
                        imageDecoder = WebpAnimatedDecoder.Factory()
                    )
                )
                add(
                    DecoderTestItem(
                        name = "HEIF_ANIMATED",
                        imageUri = AssetImages.animHeif.uri,
                        minAPI = VERSION_CODES.P,
                        imageDecoder = HeifAnimatedDecoder.Factory()
                    )
                )
                add(
                    DecoderTestItem(
                        name = "MP4_FFMPEG",
                        imageUri = AssetImages.mp4.uri,
                        minAPI = null,
                        imageDecoder = FFmpegVideoFrameDecoder.Factory()
                    )
                )
                add(
                    DecoderTestItem(
                        name = "MP4_BUILTIN",
                        imageUri = AssetImages.mp4.uri,
                        minAPI = VERSION_CODES.O_MR1,
                        imageDecoder = VideoFrameDecoder.Factory()
                    )
                )
                add(
                    DecoderTestItem(
                        name = "XML",
                        imageUri = newResourceUri(drawable.bg_circle_accent),
                        minAPI = null,
                        imageDecoder = null
                    )
                )
                add(
                    DecoderTestItem(
                        name = "VECTOR",
                        imageUri = newResourceUri(drawable.ic_play),
                        minAPI = null,
                        imageDecoder = null
                    )
                )
                val headerUserPackageInfo = loadUserAppPackageInfo(true)
                add(
                    DecoderTestItem(
                        name = "APK_ICON",
                        imageUri = headerUserPackageInfo.applicationInfo.publicSourceDir,
                        minAPI = null,
                        imageDecoder = ApkIconDecoder.Factory()
                    )
                )
            }
        }
    }

    private suspend fun loadUserAppPackageInfo(fromHeader: Boolean): PackageInfo {
        return withContext(Dispatchers.IO) {
            val packageList =
                application1.packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
            (if (fromHeader) {
                packageList.find {
                    it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0
                }
            } else {
                packageList.findLast {
                    it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0
                }
            } ?: application1.packageManager.getPackageInfo(application1.packageName, 0))
        }
    }
}

class DecoderTestItem(
    val name: String,
    val imageUri: String,
    val minAPI: Int?,
    val imageDecoder: Decoder.Factory? = null
)