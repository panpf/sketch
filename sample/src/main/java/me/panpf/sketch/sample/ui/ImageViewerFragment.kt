/*
 * Copyright (C) 2019 Peng fei Pan <panpfpanpf@outlook.me>
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

package me.panpf.sketch.sample.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.github.panpf.assemblyadapter.pager2.AssemblyFragmentStateAdapter
import com.github.panpf.tools4a.display.ktx.getScreenHeight
import com.github.panpf.tools4a.display.ktx.getScreenWidth
import com.github.panpf.tools4a.display.ktx.getStatusBarHeight
import com.github.panpf.tools4a.display.ktx.isOrientationPortrait
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.panpf.sketch.sample.image.ImageOptions
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.bean.Image
import me.panpf.sketch.sample.databinding.FragmentImageViewerBinding
import me.panpf.sketch.sample.item.ImageFragmentItemFactory
import me.panpf.sketch.sample.util.DeviceUtils
import me.panpf.sketch.sample.vm.ShowingImageChangedViewModel

class ImageViewerFragment : BaseFragment<FragmentImageViewerBinding>() {

    private val args by navArgs<ImageViewerFragmentArgs>()
    private val showingImageChangedViewModel by viewModels<ShowingImageChangedViewModel>()

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        FragmentImageViewerBinding.inflate(inflater, parent, false)

    override fun onInitViews(binding: FragmentImageViewerBinding, savedInstanceState: Bundle?) {
        super.onInitViews(binding, savedInstanceState)

        binding.imageViewerBgImage.apply {
            updateLayoutParams<ViewGroup.LayoutParams> {
                width = requireContext().getScreenWidth()
                height = requireContext().getScreenHeight()
                // + DeviceUtils.getWindowHeightSupplement(this) for MIX 2
                if (isOrientationPortrait()) {
                    height += DeviceUtils.getWindowHeightSupplement(requireActivity())
                } else {
                    width += DeviceUtils.getWindowHeightSupplement(requireActivity())
                }
            }
            setOptions(ImageOptions.WINDOW_BACKGROUND)
        }

        binding.imageViewerPager.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin += requireContext().getStatusBarHeight()
                }
            }
        }

        binding.imageViewerPageNumberText.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin += requireContext().getStatusBarHeight()
                }
            }
        }
    }

    override fun onInitData(binding: FragmentImageViewerBinding, savedInstanceState: Bundle?) {
        val imageList = Json.decodeFromString<List<Image>>(args.imageJsonArray)

        showingImageChangedViewModel.imageChangedData.observe(viewLifecycleOwner) {
            binding.imageViewerBgImage.displayImage(it)
        }

        binding.imageViewerPager.apply {
            adapter = AssemblyFragmentStateAdapter(
                this@ImageViewerFragment,
                listOf(ImageFragmentItemFactory(loadingOptionsId = args.loadingImageOptionsKey)),
                imageList
            )
            post {
                currentItem = args.defaultPosition
            }
        }

        binding.imageViewerPageNumberText.apply {
            text = "%d/%d".format(
                args.defaultPosition + 1,
                binding.imageViewerPager.adapter!!.itemCount
            )
            binding.imageViewerPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    text = "%d/%d".format(
                        position + 1,
                        binding.imageViewerPager.adapter!!.itemCount
                    )
                }
            })
        }
    }
}
