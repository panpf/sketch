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

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.fetch.newBlurHashUri
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.loadImage
import com.github.panpf.sketch.request.blurHashPlaceholder
import com.github.panpf.sketch.sample.databinding.FragmentTestBlurhashAndroidBinding
import com.github.panpf.sketch.sample.image.DelayInterceptor
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.state.IntColorDrawableStateImage
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.SketchUtils
import com.github.panpf.sketch.util.decodeBlurHashToBitmap
import com.github.panpf.sketch.util.limitSide
import kotlin.math.min

class BlurHashTestFragment : BaseToolbarBindingFragment<FragmentTestBlurhashAndroidBinding>() {

    override fun getNavigationBarInsetsView(binding: FragmentTestBlurhashAndroidBinding): View {
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(
        toolbar: Toolbar,
        binding: FragmentTestBlurhashAndroidBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "BlurHash"

        val imageFile = ResourceImages.jpeg
        val imageBlurHash = "d7D+0q5W00^h01~A~B0gInR%?G9vR%R+NH=_I;NG\$\$-o"
        val imageBlurHashUri = newBlurHashUri(
            blurHash = imageBlurHash,
            width = imageFile.size.width,
            height = imageFile.size.height
        )
        val maxSide = 200

        (binding.basicText1 to binding.basicImage1).also { (titleText, imageView) ->
            titleText.text = "Source(${imageFile.size})"
            imageView.loadImage(imageFile.uri)
        }

        (binding.basicText2 to binding.basicImage2).also { (titleText, imageView) ->
            val size = imageFile.size.limitSide(maxSide)
            titleText.text = "Keep(${size})"
            val bitmap = decodeBlurHashToBitmap(
                blurHash = imageBlurHash,
                width = size.width,
                height = size.height
            )
            imageView.setImageBitmap(bitmap)
        }

        (binding.basicText3 to binding.basicImage3).also { (titleText, imageView) ->
            val size = imageFile.size
                .let { min(a = it.width, b = it.height) }
                .let { Size(width = it, height = it) }
                .limitSide(maxSide)
            titleText.text = "Square(${size})"
            val bitmap = decodeBlurHashToBitmap(
                blurHash = imageBlurHash,
                width = size.width,
                height = size.height
            )
            imageView.setImageBitmap(bitmap)
        }

        (binding.basicText4 to binding.basicImage4).also { (titleText, imageView) ->
            val size = imageFile.size
                .let { Size(width = it.height, height = it.width) }
                .limitSide(maxSide)
            titleText.text = "Reverse(${size})"
            val bitmap = decodeBlurHashToBitmap(
                blurHash = imageBlurHash,
                width = size.width,
                height = size.height
            )
            imageView.setImageBitmap(bitmap)
        }

        (binding.basicText5 to binding.basicImage5).also { (titleText, imageView) ->
            titleText.text = "Keep-Crop"
            val size = imageFile.size.limitSide(maxSide)
            val bitmap = decodeBlurHashToBitmap(
                blurHash = imageBlurHash,
                width = size.width,
                height = size.height
            )
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setImageBitmap(bitmap)
        }

        (binding.placeholderText1 to binding.placeholderImage1).also { (titleText, imageView) ->
            titleText.text = "Fit"
            imageView.loadImage(imageFile.uri) {
                memoryCachePolicy(CachePolicy.DISABLED)
                resultCachePolicy(CachePolicy.DISABLED)
                blurHashPlaceholder(imageBlurHashUri, maxSide = maxSide)
                crossfade()
                components {
                    add(DelayInterceptor(2000))
                }
            }
        }

        (binding.placeholderText2 to binding.placeholderImage2).also { (titleText, imageView) ->
            titleText.text = "Crop"
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.loadImage(imageFile.uri) {
                memoryCachePolicy(CachePolicy.DISABLED)
                resultCachePolicy(CachePolicy.DISABLED)
                blurHashPlaceholder(imageBlurHashUri, maxSide = maxSide)
                crossfade()
                components {
                    add(DelayInterceptor(2000))
                }
            }
        }

        (binding.placeholderText3 to binding.placeholderImage3).also { (titleText, imageView) ->
            titleText.text = "Crop-Square"
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.loadImage(imageFile.uri) {
                memoryCachePolicy(CachePolicy.DISABLED)
                resultCachePolicy(CachePolicy.DISABLED)
                blurHashPlaceholder(
                    imageBlurHashUri,
                    maxSide = maxSide,
                    size = imageFile.size
                        .let { min(a = it.width, b = it.height) }
                        .let { Size(width = it, height = it) }
                        .limitSide(maxSide)
                )
                crossfade()
                components {
                    add(DelayInterceptor(2000))
                }
            }
        }

        (binding.deocdeText1 to binding.deocdeImage1).also { (titleText, imageView) ->
            titleText.text = "Fit"
            imageView.loadImage(imageBlurHashUri) {
                memoryCachePolicy(CachePolicy.DISABLED)
                resultCachePolicy(CachePolicy.DISABLED)
                placeholder(IntColorDrawableStateImage(Color.TRANSPARENT))
                crossfade()
                components {
                    add(DelayInterceptor(2000))
                }
            }
        }

        (binding.deocdeText2 to binding.deocdeImage2).also { (titleText, imageView) ->
            titleText.text = "Crop"
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.loadImage(imageBlurHashUri) {
                memoryCachePolicy(CachePolicy.DISABLED)
                resultCachePolicy(CachePolicy.DISABLED)
                placeholder(IntColorDrawableStateImage(Color.TRANSPARENT))
                crossfade()
                components {
                    add(DelayInterceptor(2000))
                }
            }
        }

        (binding.deocdeText3 to binding.deocdeImage3).also { (titleText, imageView) ->
            titleText.text = "ALPHA_8"
            imageView.loadImage(imageBlurHashUri) {
                memoryCachePolicy(CachePolicy.DISABLED)
                resultCachePolicy(CachePolicy.DISABLED)
                placeholder(IntColorDrawableStateImage(Color.TRANSPARENT))
                colorType(alpha8ColorType)
                crossfade()
                components {
                    add(DelayInterceptor(2000))
                }
            }
        }

        binding.placeholderRefreshIcon.setOnClickListener {
            SketchUtils.restart(binding.placeholderImage1)
            SketchUtils.restart(binding.placeholderImage2)
            SketchUtils.restart(binding.placeholderImage3)
        }

        binding.decodeRefreshIcon.setOnClickListener {
            SketchUtils.restart(binding.deocdeImage1)
            SketchUtils.restart(binding.deocdeImage2)
            SketchUtils.restart(binding.deocdeImage3)
        }
    }
}