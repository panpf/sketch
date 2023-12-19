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

import android.os.Bundle
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.decode.BitmapDecodeInterceptor
import com.github.panpf.sketch.decode.BitmapDecodeInterceptor.Chain
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.FragmentTestAnimatablePlaceholderBinding
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import kotlinx.coroutines.delay

class AnimatablePlaceholderTestFragment :
    BaseToolbarBindingFragment<FragmentTestAnimatablePlaceholderBinding>() {

    private var urlIndex = 0

    override fun onViewCreated(
        toolbar: androidx.appcompat.widget.Toolbar,
        binding: FragmentTestAnimatablePlaceholderBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "AnimatablePlaceholder"

        displayImage(binding)

        binding.retryButton.setOnClickListener {
            urlIndex++
            displayImage(binding)
        }
    }

    private fun displayImage(binding: FragmentTestAnimatablePlaceholderBinding) {
        val urlString = AssetImages.statics[urlIndex % AssetImages.statics.size]
        binding.myImage.displayImage(urlString) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            placeholder(R.drawable.ic_placeholder_eclipse_animated)
            components {
                addBitmapDecodeInterceptor(object : BitmapDecodeInterceptor {
                    override val key: String?
                        get() = null
                    override val sortWeight: Int
                        get() = 0

                    override suspend fun intercept(chain: Chain): Result<BitmapDecodeResult> {
                        delay(5000)
                        return chain.proceed()
                    }
                })
            }
        }
    }
}