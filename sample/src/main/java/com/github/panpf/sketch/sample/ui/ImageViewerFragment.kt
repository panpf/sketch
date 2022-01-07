/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.sample.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.github.panpf.assemblyadapter.pager2.AssemblyFragmentStateAdapter
import com.github.panpf.sketch.sample.base.BindingFragment
import com.github.panpf.sketch.sample.bean.Image
import com.github.panpf.sketch.sample.bean.ImageDetail
import com.github.panpf.sketch.sample.databinding.FragmentImageViewerBinding
import com.github.panpf.sketch.sample.item.ImageDetailFragmentItemFactory
import com.github.panpf.tools4a.display.ktx.getStatusBarHeight
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ImageViewerFragment : BindingFragment<FragmentImageViewerBinding>() {

    private val args by navArgs<ImageViewerFragmentArgs>()
//    private val showingImageChangedViewModel by viewModels<ShowingImageChangedViewModel>()

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        FragmentImageViewerBinding.inflate(inflater, parent, false)

    override fun onInitViews(binding: FragmentImageViewerBinding, savedInstanceState: Bundle?) {
        super.onInitViews(binding, savedInstanceState)

//        binding.imageViewerBgImage.apply {
//            updateLayoutParams<ViewGroup.LayoutParams> {
//                width = requireContext().getScreenWidth()
//                height = requireContext().getScreenHeight()
//                // + DeviceUtils.getNavigationBarHeight(requireActivity()) for MIX 2
//                val windowHeightSupplement =
//                    if (requireActivity().window.decorView.systemUiVisibility == View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) {
//                        Displayx.getNavigationBarHeight(requireActivity())
//                    } else {
//                        0
//                    }
//                if (isOrientationPortrait()) {
//                    height += windowHeightSupplement
//                } else {
//                    width += windowHeightSupplement
//                }
//            }
////            setOptions(ImageOptions.WINDOW_BACKGROUND)
//        }

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
        val imageList = Json.decodeFromString<List<ImageDetail>>(args.imageDetailJsonArray)

//        showingImageChangedViewModel.imageChangedData.observe(viewLifecycleOwner) {
//            binding.imageViewerBgImage.displayImage(it)
//        }

        binding.imageViewerPager.apply {
            adapter = AssemblyFragmentStateAdapter(
                this@ImageViewerFragment,
                listOf(ImageDetailFragmentItemFactory()),
                imageList
            )
            post {
                setCurrentItem(args.defaultPosition, false)
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
