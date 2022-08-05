
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
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.github.panpf.assemblyadapter.pager2.AssemblyFragmentStateAdapter
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.sample.databinding.ImageViewerPagerFragmentBinding
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.github.panpf.sketch.stateimage.CurrentStateImage
import com.github.panpf.sketch.transform.BlurTransformation
import com.github.panpf.tools4a.display.ktx.getScreenHeight
import com.github.panpf.tools4a.display.ktx.getScreenWidth
import com.github.panpf.tools4a.display.ktx.getStatusBarHeight
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ImageViewerPagerFragment : BindingFragment<ImageViewerPagerFragmentBinding>() {

    private val args by navArgs<ImageViewerPagerFragmentArgs>()
    private val viewModel by viewModels<ImageViewerPagerViewModel>()
    private val swipeExitViewModel by viewModels<ImageViewerSwipeExitViewModel>()

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
                    val imageUrl = imageList[position].let { it.middenUrl ?: it.url }
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

        swipeExitViewModel.progressChangedEvent.listen(viewLifecycleOwner) {
            val progress = it ?: 0f
            binding.root.background = binding.root.background?.mutate()?.apply {
                alpha = ((1 - progress) * 255).toInt()
            }
            binding.imageViewerBgImage.alpha = 1 - progress
            binding.imageViewerPagerTools.alpha = 1 - progress
        }

        swipeExitViewModel.backEvent.listen(viewLifecycleOwner) {
            requireActivity().finish()
        }
    }
}
