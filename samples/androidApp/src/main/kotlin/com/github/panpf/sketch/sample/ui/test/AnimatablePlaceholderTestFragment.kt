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

package com.github.panpf.sketch.sample.ui.test

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.loadImage
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.FragmentTestAnimatablePlaceholderBinding
import com.github.panpf.sketch.sample.image.DelayInterceptor
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.components.NewMoonLoadingDrawable
import com.github.panpf.sketch.state.IconAnimatableDrawableStateImage
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4a.dimen.ktx.dp2px

class AnimatablePlaceholderTestFragment :
    BaseToolbarBindingFragment<FragmentTestAnimatablePlaceholderBinding>() {

    private var urlIndex = 0

    override fun getNavigationBarInsetsView(binding: FragmentTestAnimatablePlaceholderBinding): View {
        return binding.root
    }

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: FragmentTestAnimatablePlaceholderBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "AnimatablePlaceholder"

        loadImage(binding)

        binding.retryButton.setOnClickListener {
            urlIndex++
            loadImage(binding)
        }
    }

    private fun loadImage(binding: FragmentTestAnimatablePlaceholderBinding) {
        val images =
            arrayOf(
                ComposeResImageFiles.jpeg.uri,
                ComposeResImageFiles.webp.uri,
                ComposeResImageFiles.bmp.uri
            )
        val urlString = images[urlIndex % images.size]
        binding.myImage1.loadImage(urlString) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            placeholder(
                IconAnimatableDrawableStateImage(
                    icon = NewMoonLoadingDrawable(Size(24.dp2px, 24.dp2px))
                        .asEquitable("NewMoonLoadingDrawable"),
                    background = R.color.placeholder_bg,
                    iconTint = R.color.placeholder_icon
                )
            )
            components {
                add(DelayInterceptor(3000))
            }
        }
        binding.myImage2.loadImage(urlString) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            placeholder(
                IconAnimatableDrawableStateImage(
                    icon = NewMoonLoadingDrawable(Size(24.dp2px, 24.dp2px))
                        .asEquitable("NewMoonLoadingDrawable"),
                    background = R.color.placeholder_bg,
                    iconTint = R.color.placeholder_icon
                )
            )
            components {
                add(DelayInterceptor(3000))
            }
        }
        binding.myImage3.loadImage(urlString) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            placeholder(
                IconAnimatableDrawableStateImage(
                    icon = NewMoonLoadingDrawable(Size(24.dp2px, 24.dp2px))
                        .asEquitable("NewMoonLoadingDrawable"),
                    background = R.color.placeholder_bg,
                    iconTint = R.color.placeholder_icon
                )
            )
            components {
                add(DelayInterceptor(3000))
            }
        }
    }
}