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
package com.github.panpf.sketch.sample.ui.viewer.view

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.github.panpf.assemblyadapter.pager2.AssemblyFragmentStateAdapter
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.request.LoadState
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.databinding.FragmentImagePagerBinding
import com.github.panpf.sketch.sample.eventService
import com.github.panpf.sketch.sample.image.PaletteBitmapDecodeInterceptor
import com.github.panpf.sketch.sample.image.simplePalette
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.ui.MainFragmentDirections
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.ui.setting.Page
import com.github.panpf.sketch.sample.ui.viewer.ImagePagerViewModel
import com.github.panpf.sketch.sample.ui.viewer.view.ImageViewerFragment.ItemFactory
import com.github.panpf.sketch.sample.util.WithDataActivityResultContracts
import com.github.panpf.sketch.sample.util.registerForActivityResult
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.stateimage.CurrentStateImage
import com.github.panpf.sketch.transform.BlurTransformation
import com.github.panpf.tools4a.display.ktx.getScreenSize
import com.github.panpf.tools4a.display.ktx.getStatusBarHeight
import com.github.panpf.tools4a.toast.ktx.showLongToast
import com.github.panpf.tools4k.lang.asOrThrow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class ImagePagerFragment : BaseBindingFragment<FragmentImagePagerBinding>() {

    private val args by navArgs<ImagePagerFragmentArgs>()
    private val imageList by lazy {
        Json.decodeFromString<List<ImageDetail>>(args.imageDetailJsonArray)
    }
    private val viewModel by viewModels<ImagePagerViewModel>()
    private val requestPermissionResult =
        registerForActivityResult(WithDataActivityResultContracts.RequestPermission())

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(
        binding: FragmentImagePagerBinding,
        savedInstanceState: Bundle?
    ) {
        binding.pager.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin += requireContext().getStatusBarHeight()
                }
            }
            adapter = AssemblyFragmentStateAdapter(
                this@ImagePagerFragment,
                listOf(ItemFactory()),
                imageList
            )

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    val imageUrl =
                        imageList[position].let { it.thumbnailUrl ?: it.mediumUrl ?: it.originUrl }
                    loadBgImage(binding, imageUrl)
                }
            })

            post {
                val initialItem = args.initialPosition - args.startPosition
                setCurrentItem(initialItem, false)
            }
        }

        binding.bgImage.requestState.loadState.repeatCollectWithLifecycle(
            viewLifecycleOwner,
            State.STARTED
        ) {
            if (it is LoadState.Success) {
                val simplePalette = it.result.simplePalette
                val accentColor =
                    simplePalette?.dominantSwatch?.rgb
                        ?: simplePalette?.lightVibrantSwatch?.rgb
                        ?: simplePalette?.vibrantSwatch?.rgb
                        ?: simplePalette?.lightMutedSwatch?.rgb
                        ?: simplePalette?.mutedSwatch?.rgb
                        ?: simplePalette?.darkVibrantSwatch?.rgb
                        ?: simplePalette?.darkMutedSwatch?.rgb
                changeButtonBg(
                    binding,
                    accentColor ?: Color.parseColor("#bf5660")
                )
            } else if (it is LoadState.Error) {
                changeButtonBg(binding, Color.parseColor("#bf5660"))
            }
        }

        binding.toolsLayout.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin += requireContext().getStatusBarHeight()
                }
            }
        }

        binding.pageNumberText.apply {
            val updateCurrentPageNumber: () -> Unit = {
                val pageNumber = args.startPosition + binding.pager.currentItem + 1
                text = "$pageNumber/${args.totalCount}"
            }
            binding.pager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateCurrentPageNumber()
                }
            })
            updateCurrentPageNumber()
        }

        binding.shareImage.setOnClickListener {
            share(imageList[binding.pager.currentItem])
        }

        binding.saveImage.setOnClickListener {
            save(imageList[binding.pager.currentItem])
        }

        binding.rotateImage.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                eventService.viewerPagerRotateEvent.emit(0)
            }
        }

        binding.infoImage.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                eventService.viewerPagerInfoEvent.emit(binding.pager.currentItem)
            }
        }

        binding.originImage.apply {
            appSettingsService.showOriginImage.stateFlow
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    setImageResource(if (it) R.drawable.ic_image_2_fill else R.drawable.ic_image_2)
                }

            setOnClickListener {
                val newValue = !appSettingsService.showOriginImage.value
                appSettingsService.showOriginImage.value = newValue
                if (newValue) {
                    showLongToast("Opened View original image")
                } else {
                    showLongToast("Closed View original image")
                }
            }
        }

        binding.settingsImage.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionGlobalSettingsDialogFragment(Page.ZOOM.name)
            )
        }
    }

    private fun loadBgImage(binding: FragmentImagePagerBinding, imageUrl: String) {
        binding.bgImage.displayImage(imageUrl) {
            val screenSize = requireContext().getScreenSize()
            resize(
                width = screenSize.x / 4,
                height = screenSize.y / 4,
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
                addBitmapDecodeInterceptor(PaletteBitmapDecodeInterceptor())
            }
        }
    }

    private fun changeButtonBg(binding: FragmentImagePagerBinding, color: Int) {
        val finalInt = ColorUtils.setAlphaComponent(color, 160)
        listOf(
            binding.settingsImage,
            binding.originImage,
            binding.shareImage,
            binding.saveImage,
            binding.rotateImage,
            binding.infoImage,
            binding.pageNumberText,
        ).forEach {
            it.background.asOrThrow<GradientDrawable>().setColor(finalInt)
        }
    }

    private fun share(image: ImageDetail) {
        val imageUri = if (appSettingsService.showOriginImage.value) {
            image.originUrl
        } else {
            image.mediumUrl ?: image.originUrl
        }
        lifecycleScope.launch {
            handleActionResult(viewModel.share(imageUri))
        }
    }

    private fun save(image: ImageDetail) {
        val imageUri = if (appSettingsService.showOriginImage.value) {
            image.originUrl
        } else {
            image.mediumUrl ?: image.originUrl
        }
        val input = WithDataActivityResultContracts.RequestPermission.Input(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ) {
            lifecycleScope.launch {
                handleActionResult(viewModel.save(imageUri))
            }
        }
        requestPermissionResult.launch(input)
    }
}