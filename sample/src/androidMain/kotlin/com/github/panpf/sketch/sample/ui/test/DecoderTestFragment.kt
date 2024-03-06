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
package com.github.panpf.sketch.sample.ui.test

import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.viewModelScope
import com.github.panpf.assemblyadapter.pager2.AssemblyFragmentStateAdapter
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.ApkIconDecoder
import com.github.panpf.sketch.decode.FFmpegVideoFrameDecoder
import com.github.panpf.sketch.decode.GifAnimatedDecoder
import com.github.panpf.sketch.decode.GifDrawableDecoder
import com.github.panpf.sketch.decode.GifMovieDecoder
import com.github.panpf.sketch.decode.HeifAnimatedDecoder
import com.github.panpf.sketch.decode.SvgDecoder.Factory
import com.github.panpf.sketch.decode.VideoFrameDecoder
import com.github.panpf.sketch.decode.WebpAnimatedDecoder
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.sample.R.drawable
import com.github.panpf.sketch.sample.databinding.FragmentTabPagerBinding
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DecoderTestFragment : BaseToolbarBindingFragment<FragmentTabPagerBinding>() {

    private val viewModel by viewModels<DecoderTestViewModel>()

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: FragmentTabPagerBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Decoder"

        viewModel.data.repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
            binding.pager.adapter = AssemblyFragmentStateAdapter(
                fragment = this,
                itemFactoryList = listOf(DecoderTestImageFragment.ItemFactory()),
                initDataList = List(it.size) { position -> position }
            )

            TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
                tab.text = it[position].name
            }.attach()
        }
    }

    class DecoderTestViewModel(application1: Application) :
        LifecycleAndroidViewModel(application1) {

        private val _data = MutableStateFlow<List<DecoderTestItem>>(emptyList())
        val data: StateFlow<List<DecoderTestItem>> = _data

        init {
            viewModelScope.launch {
                _data.value = buildDecoderTestItems(application1)
            }
        }
    }
}

@SuppressLint("NewApi")
actual suspend fun buildDecoderTestItems(context: PlatformContext): List<DecoderTestItem> =
    buildList {
        add(DecoderTestItem(name = "JPEG", imageUri = AssetImages.jpeg.uri))
        add(DecoderTestItem(name = "PNG", imageUri = AssetImages.png.uri))
        add(DecoderTestItem(name = "WEBP", imageUri = AssetImages.webp.uri))
        add(DecoderTestItem(name = "BMP", imageUri = AssetImages.bmp.uri))
        add(
            DecoderTestItem(
                name = "SVG",
                imageUri = AssetImages.svg.uri,
                imageDecoder = Factory()
            )
        )
        add(
            DecoderTestItem(
                name = "HEIC",
                imageUri = AssetImages.heic.uri,
                minAPI = VERSION_CODES.P,
                currentApi = VERSION.SDK_INT,
            )
        )
        add(
            DecoderTestItem(
                name = "GIF_KORAL",
                imageUri = AssetImages.animGif.uri,
                imageDecoder = GifDrawableDecoder.Factory()
            )
        )
        add(
            DecoderTestItem(
                name = "GIF_MOVIE",
                imageUri = AssetImages.animGif.uri,
                minAPI = VERSION_CODES.KITKAT,
                currentApi = VERSION.SDK_INT,
                imageDecoder = GifMovieDecoder.Factory()
            )
        )
        add(
            DecoderTestItem(
                name = "GIF_ANIMATED",
                imageUri = AssetImages.animGif.uri,
                minAPI = VERSION_CODES.P,
                currentApi = VERSION.SDK_INT,
                imageDecoder = GifAnimatedDecoder.Factory()
            )
        )
        add(
            DecoderTestItem(
                name = "WEBP_ANIMATED",
                imageUri = AssetImages.animWebp.uri,
                minAPI = VERSION_CODES.P,
                currentApi = VERSION.SDK_INT,
                imageDecoder = WebpAnimatedDecoder.Factory()
            )
        )
        add(
            DecoderTestItem(
                name = "HEIF_ANIMATED",
                imageUri = AssetImages.animHeif.uri,
                minAPI = VERSION_CODES.P,
                currentApi = VERSION.SDK_INT,
                imageDecoder = HeifAnimatedDecoder.Factory()
            )
        )
        add(
            DecoderTestItem(
                name = "MP4_FFMPEG",
                imageUri = AssetImages.mp4.uri,
                imageDecoder = FFmpegVideoFrameDecoder.Factory()
            )
        )
        add(
            DecoderTestItem(
                name = "MP4_BUILTIN",
                imageUri = AssetImages.mp4.uri,
                minAPI = VERSION_CODES.O_MR1,
                currentApi = VERSION.SDK_INT,
                imageDecoder = VideoFrameDecoder.Factory()
            )
        )
        add(
            DecoderTestItem(
                name = "XML",
                imageUri = newResourceUri(drawable.bg_circle_accent),
            )
        )
        add(
            DecoderTestItem(
                name = "VECTOR",
                imageUri = newResourceUri(drawable.ic_play),
            )
        )
        val headerUserPackageInfo = loadUserAppPackageInfo(context, true)
        add(
            DecoderTestItem(
                name = "APK_ICON",
                imageUri = headerUserPackageInfo.applicationInfo.publicSourceDir,
                imageDecoder = ApkIconDecoder.Factory()
            )
        )
    }

private suspend fun loadUserAppPackageInfo(
    context: PlatformContext,
    fromHeader: Boolean
): PackageInfo {
    return withContext(Dispatchers.IO) {
        val packageList =
            context.packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        (if (fromHeader) {
            packageList.find {
                it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0
            }
        } else {
            packageList.findLast {
                it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0
            }
        } ?: context.packageManager.getPackageInfo(context.packageName, 0))
    }
}