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

package com.github.panpf.sketch.sample.ui.gallery

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.github.panpf.assemblyadapter.pager2.AssemblyFragmentStateAdapter
import com.github.panpf.sketch.loadImage
import com.github.panpf.sketch.request.LoadState
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.databinding.FragmentImagePagerBinding
import com.github.panpf.sketch.sample.image.PaletteDecodeInterceptor
import com.github.panpf.sketch.sample.image.palette.PhotoPalette
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

class PhotoPagerFragment : BaseBindingFragment<FragmentImagePagerBinding>() {

    private val args by navArgs<PhotoPagerFragmentArgs>()
    private val photoList by lazy {
        Json.decodeFromString<List<Photo>>(args.photos)
    }
    private val photoPaletteViewModel by viewModels<PhotoPaletteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lightStatusAndNavigationBar = false
    }

    override fun getStatusBarInsetsView(binding: FragmentImagePagerBinding): View {
        return binding.statusBarInsetsLayout
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(
        binding: FragmentImagePagerBinding,
        savedInstanceState: Bundle?
    ) {
        binding.pager.apply {
            adapter = AssemblyFragmentStateAdapter(
                fragment = this@PhotoPagerFragment,
                itemFactoryList = listOf(PhotoViewerFragment.ItemFactory()),
                initDataList = photoList
            )

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    val imageUrl = photoList[position].listThumbnailUrl
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
                photoPaletteViewModel.setPhotoPalette(
                    PhotoPalette(
                        palette = it.result.simplePalette,
                        primaryColor = ResourcesCompat.getColor(
                            resources, R.color.md_theme_primary, null
                        ),
                        primaryContainerColor = ResourcesCompat.getColor(
                            resources, R.color.md_theme_primaryContainer, null
                        )
                    )
                )
            }
        }

        binding.pageNumberText.apply {
            val updateCurrentPageNumber: () -> Unit = {
                val pageNumber = args.startPosition + binding.pager.currentItem + 1
                text = resources.getString(R.string.pager_number_ver, pageNumber, args.totalCount)
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
            appSettings.showOriginImage
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    setImageResource(if (it) R.drawable.ic_image2_baseline else R.drawable.ic_image2_outline)
                }

            setOnClickListener {
                val newValue = !appSettings.showOriginImage.value
                appSettings.showOriginImage.value = newValue
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

        photoPaletteViewModel.photoPaletteState.repeatCollectWithLifecycle(
            owner = viewLifecycleOwner,
            state = State.STARTED
        ) { photoPalette ->
            listOf(
                binding.backImage,
                binding.settingsImage,
                binding.originImage,
                binding.pageNumberText
            ).forEach {
                it.background.asOrThrow<GradientDrawable>().setColor(photoPalette.containerColorInt)
            }
        }
    }

    private fun loadBgImage(binding: FragmentImagePagerBinding, imageUrl: String) {
        binding.bgImage.loadImage(imageUrl) {
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