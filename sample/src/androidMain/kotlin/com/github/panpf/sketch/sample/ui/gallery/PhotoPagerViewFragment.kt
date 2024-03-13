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
package com.github.panpf.sketch.sample.ui.gallery

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.github.panpf.assemblyadapter.pager2.AssemblyFragmentStateAdapter
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.request.LoadState
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.databinding.FragmentImagePagerBinding
import com.github.panpf.sketch.sample.image.PaletteDecodeInterceptor
import com.github.panpf.sketch.sample.image.simplePalette
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.sample.ui.setting.Page
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.transform.BlurTransformation
import com.github.panpf.tools4a.display.ktx.getScreenSize
import com.github.panpf.tools4a.toast.ktx.showLongToast
import com.github.panpf.tools4k.lang.asOrThrow
import kotlinx.serialization.json.Json

class PhotoPagerViewFragment : BaseBindingFragment<FragmentImagePagerBinding>() {

    private val args by navArgs<PhotoPagerViewFragmentArgs>()
    private val imageList by lazy {
        Json.decodeFromString<List<Photo>>(args.photos)
    }
    private val viewModel by viewModels<PhotoPagerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lightStatusAndNavigationBar = false
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(
        binding: FragmentImagePagerBinding,
        savedInstanceState: Bundle?
    ) {
        binding.pager.apply {
            adapter = AssemblyFragmentStateAdapter(
                this@PhotoPagerViewFragment,
                listOf(PhotoViewerViewFragment.ItemFactory()),
                imageList
            )

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    val imageUrl = imageList[position]
                        .let { it.thumbnailUrl ?: it.mediumUrl ?: it.originalUrl }
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
                val preferredSwatch = it.result.simplePalette?.run {
                    listOfNotNull(
                        darkMutedSwatch,
                        mutedSwatch,
                        lightMutedSwatch,
                        darkVibrantSwatch,
                        vibrantSwatch,
                        lightVibrantSwatch,
                    ).firstOrNull()
                }
                if (preferredSwatch != null) {
                    viewModel.setButtonBgColor(preferredSwatch.rgb)
                }
            }
        }

        binding.pageNumberText.apply {
            val updateCurrentPageNumber: () -> Unit = {
                val pageNumber = args.startPosition + binding.pager.currentItem + 1
                text = context.resources.getString(
                    R.string.pager_number_ver,
                    pageNumber.coerceAtMost(999),
                    args.totalCount.coerceAtMost(999)
                )
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

        binding.originImage.apply {
            appSettingsService.showOriginImage
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    setImageResource(if (it) R.drawable.ic_image2_baseline else R.drawable.ic_image2_outline)
                }

            setOnClickListener {
                val newValue = !appSettingsService.showOriginImage.value
                appSettingsService.showOriginImage.value = newValue
                if (newValue) {
                    showLongToast("Now show original image")
                } else {
                    showLongToast("Now show thumbnails image")
                }
            }
        }

        binding.settingsImage.setOnClickListener {
            findNavController().navigate(
                NavMainDirections.actionSettingsDialogFragment(Page.ZOOM.name)
            )
        }

        binding.backImage.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.buttonBgColor.repeatCollectWithLifecycle(
            owner = viewLifecycleOwner,
            state = State.STARTED
        ) { color ->
            listOf(
                binding.backImage,
                binding.settingsImage,
                binding.originImage,
                binding.pageNumberText
            ).forEach {
                it.background.asOrThrow<GradientDrawable>().setColor(color)
            }
        }
    }

    override fun getStatusBarInsetsView(binding: FragmentImagePagerBinding): View {
        return binding.statusBarInsetsLayout
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
            disallowAnimatedImage()
            crossfade(alwaysUse = true, durationMillis = 400)
            resizeOnDraw()
            components {
                addDecodeInterceptor(PaletteDecodeInterceptor())
            }
        }
    }
}