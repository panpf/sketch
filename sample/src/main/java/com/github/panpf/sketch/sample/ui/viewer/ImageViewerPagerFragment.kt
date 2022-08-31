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
package com.github.panpf.sketch.sample.ui.viewer

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.github.panpf.assemblyadapter.pager2.AssemblyFragmentStateAdapter
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.sample.databinding.ImageViewerPagerFragmentBinding
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.prefsService
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.github.panpf.sketch.sample.util.PaletteBitmapDecoderInterceptor
import com.github.panpf.sketch.sample.util.simplePalette
import com.github.panpf.sketch.stateimage.CurrentStateImage
import com.github.panpf.sketch.transform.BlurTransformation
import com.github.panpf.tools4a.display.ktx.getScreenHeight
import com.github.panpf.tools4a.display.ktx.getScreenWidth
import com.github.panpf.tools4a.display.ktx.getStatusBarHeight
import com.github.panpf.tools4a.toast.ktx.showLongToast
import com.github.panpf.tools4k.lang.asOrThrow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ImageViewerPagerFragment : BindingFragment<ImageViewerPagerFragmentBinding>() {

    private val args by navArgs<ImageViewerPagerFragmentArgs>()
    private val viewModel by viewModels<ImageViewerPagerViewModel>()

    override fun onViewCreated(
        binding: ImageViewerPagerFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        binding.imageViewerPagerPager.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin += requireContext().getStatusBarHeight()
                }
            }
            val imageList = Json.decodeFromString<List<ImageDetail>>(args.imageDetailJsonArray)
            adapter = AssemblyFragmentStateAdapter(
                this@ImageViewerPagerFragment,
                listOf(ImageViewerFragment.ItemFactory()),
                imageList
            )

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    val imageUrl =
                        imageList[position].let { it.bgUrl ?: it.previewUrl ?: it.originUrl }
                    binding.imageViewerBgImage.displayImage(imageUrl) {
                        resize(
                            requireContext().getScreenWidth() / 4,
                            requireContext().getScreenHeight() / 4,
                            precision = LESS_PIXELS
                        )
                        addTransformations(
                            BlurTransformation(
                                radius = 20,
                                maskColor = ColorUtils.setAlphaComponent(Color.BLACK, 100)
                            )
                        )
                        placeholder(CurrentStateImage())
                        disallowAnimatedImage()
                        crossfade(alwaysUse = true, durationMillis = 400)
                        components {
                            addBitmapDecodeInterceptor(PaletteBitmapDecoderInterceptor())
                        }
                        listener(
                            onSuccess = { _, result ->
                                val simplePalette = result.simplePalette
                                val accentColor =
                                    simplePalette?.lightVibrantSwatch?.rgb
                                        ?: simplePalette?.vibrantSwatch?.rgb
                                        ?: simplePalette?.lightMutedSwatch?.rgb
                                        ?: simplePalette?.mutedSwatch?.rgb
                                        ?: simplePalette?.dominantSwatch?.rgb
                                        ?: simplePalette?.darkVibrantSwatch?.rgb
                                        ?: simplePalette?.darkMutedSwatch?.rgb
                                changeButtonBg(
                                    binding,
                                    accentColor ?: Color.parseColor("#bf5660")
                                )
                            },
                            onError = { _, _ ->
                                changeButtonBg(binding, Color.parseColor("#bf5660"))
                            }
                        )
                    }
                }
            })

            val currentItem =
                imageList.indexOfFirst { it.position == args.defaultPosition }.takeIf { it != -1 }
                    ?: 0
            post {
                setCurrentItem(currentItem, false)
            }
        }

        binding.imageViewerPagerTools.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin += requireContext().getStatusBarHeight()
                }
            }
        }

        binding.imageViewerPagerPageNumber.apply {
            text = "%d/%d".format(
                args.defaultPosition + 1,
                binding.imageViewerPagerPager.adapter!!.itemCount
            )
            binding.imageViewerPagerPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    text = "%d/%d"
                        .format(position + 1, binding.imageViewerPagerPager.adapter!!.itemCount)
                }
            })
        }

        binding.imageViewerPagerShare.setOnClickListener {
            viewModel.shareEvent.value = 0
        }

        binding.imageViewerPagerSave.setOnClickListener {
            viewModel.saveEvent.value = 0
        }

        binding.imageViewerPagerRotate.setOnClickListener {
            viewModel.rotateEvent.value = 0
        }

        binding.imageViewerPagerInfo.setOnClickListener {
            viewModel.infoEvent.value = 0
        }

        binding.imageViewerPagerOrigin.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(State.STARTED) {
                    prefsService.showOriginImage.stateFlow.collect {
                        isSelected = it
                    }
                }
            }

            setOnClickListener {
                prefsService.showOriginImage.value = !prefsService.showOriginImage.value
                if (prefsService.showOriginImage.value) {
                    showLongToast("Opened View original image")
                } else {
                    showLongToast("Closed View original image")
                }
            }
        }
    }

    private fun changeButtonBg(binding: ImageViewerPagerFragmentBinding, color: Int) {
        val finalInt = ColorUtils.setAlphaComponent(color, 160)
        listOf(
            binding.imageViewerPagerOrigin,
            binding.imageViewerPagerShare,
            binding.imageViewerPagerSave,
            binding.imageViewerPagerRotate,
            binding.imageViewerPagerInfo,
            binding.imageViewerPagerPageNumber,
        ).forEach {
            it.background.asOrThrow<GradientDrawable>().setColor(finalInt)
        }
    }
}
