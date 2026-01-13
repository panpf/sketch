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

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.View
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.loadImage
import com.github.panpf.sketch.request.animatedTransformation
import com.github.panpf.sketch.request.onAnimationEnd
import com.github.panpf.sketch.request.onAnimationStart
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.FragmentTestAnimatedImageBinding
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.sample.util.platformGifDecoders
import com.github.panpf.sketch.state.IconDrawableStateImage

class AnimatedImageTestFragment :
    BaseToolbarBindingFragment<FragmentTestAnimatedImageBinding>() {

    override fun getNavigationBarInsetsView(binding: FragmentTestAnimatedImageBinding): View {
        return binding.root
    }

    override fun onViewCreated(
        toolbar: androidx.appcompat.widget.Toolbar,
        binding: FragmentTestAnimatedImageBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "AnimatedImage"

        binding.formatGifImage.loadImage(ResourceImages.animGif.uri) {
            placeholder(
                IconDrawableStateImage(
                    icon = R.drawable.ic_image_outline,
                    background = R.color.md_theme_primaryContainer,
                    iconTint = R.color.md_theme_onPrimaryContainer
                )
            )
            error(
                IconDrawableStateImage(
                    icon = R.drawable.ic_image_broken_outline,
                    background = R.color.md_theme_primaryContainer,
                    iconTint = R.color.md_theme_onPrimaryContainer
                )
            )
        }
        binding.formatWebpImage.loadImage(ResourceImages.animWebp.uri) {
            placeholder(
                IconDrawableStateImage(
                    icon = R.drawable.ic_image_outline,
                    background = R.color.md_theme_primaryContainer,
                    iconTint = R.color.md_theme_onPrimaryContainer
                )
            )
            error(
                IconDrawableStateImage(
                    icon = R.drawable.ic_image_broken_outline,
                    background = R.color.md_theme_primaryContainer,
                    iconTint = R.color.md_theme_onPrimaryContainer
                )
            )
        }
        binding.formatHeifImage.loadImage(ResourceImages.animHeif.uri) {
            placeholder(
                IconDrawableStateImage(
                    icon = R.drawable.ic_image_outline,
                    background = R.color.md_theme_primaryContainer,
                    iconTint = R.color.md_theme_onPrimaryContainer
                )
            )
            error(
                IconDrawableStateImage(
                    icon = R.drawable.ic_image_broken_outline,
                    background = R.color.md_theme_primaryContainer,
                    iconTint = R.color.md_theme_onPrimaryContainer
                )
            )
        }

        val repeatCountTexts = listOf(
            binding.repeatCount1Text,
            binding.repeatCount2Text,
            binding.repeatCount3Text
        )
        val repeatCountImages = listOf(
            binding.repeatCount1Image,
            binding.repeatCount2Image,
            binding.repeatCount3Image
        )
        platformGifDecoders().forEachIndexed { index, factory ->
            repeatCountTexts[index].text = factory.toString()
            repeatCountImages[index].loadImage(ResourceImages.numbersGif.uri) {
                placeholder(
                    IconDrawableStateImage(
                        icon = R.drawable.ic_image_outline,
                        background = R.color.md_theme_primaryContainer,
                        iconTint = R.color.md_theme_onPrimaryContainer
                    )
                )
                error(
                    IconDrawableStateImage(
                        icon = R.drawable.ic_image_broken_outline,
                        background = R.color.md_theme_primaryContainer,
                        iconTint = R.color.md_theme_onPrimaryContainer
                    )
                )
                components {
                    add(factory)
                }
                repeatCount(0)
            }
        }

        val callbackTexts = listOf(
            binding.callback1Text,
            binding.callback2Text,
            binding.callback3Text
        )
        val callbackImages = listOf(
            binding.callback1Image,
            binding.callback2Image,
            binding.callback3Image
        )
        val callbackButtons = listOf(
            binding.play1Button,
            binding.play2Button,
            binding.play3Button
        )
        val playings = arrayOf(false, false, false)
        platformGifDecoders().forEachIndexed { index, factory ->
            callbackTexts[index].text = factory.toString()

            callbackImages[index].loadImage(ResourceImages.numbersGif.uri) {
                placeholder(
                    IconDrawableStateImage(
                        icon = R.drawable.ic_image_outline,
                        background = R.color.md_theme_primaryContainer,
                        iconTint = R.color.md_theme_onPrimaryContainer
                    )
                )
                error(
                    IconDrawableStateImage(
                        icon = R.drawable.ic_image_broken_outline,
                        background = R.color.md_theme_primaryContainer,
                        iconTint = R.color.md_theme_onPrimaryContainer
                    )
                )
                components {
                    add(factory)
                }
                repeatCount(0)
                onAnimationStart {
                    playings[index] = true
                    callbackButtons[index].text = if (playings[index]) "Stop" else "Play"
                }
                onAnimationEnd {
                    playings[index] = false
                    callbackButtons[index].text = if (playings[index]) "Stop" else "Play"
                }
            }

            callbackButtons[index].text = if (playings[index]) "Stop" else "Play"
            callbackButtons[index].setOnClickListener {
                if (playings[index]) {
                    (callbackImages[index].drawable as? Animatable)?.stop()
                } else {
                    (callbackImages[index].drawable as? Animatable)?.start()
                }
            }
        }

        val transformationTexts = listOf(
            binding.transformation1Text,
            binding.transformation2Text,
            binding.transformation3Text
        )
        val transformationImages = listOf(
            binding.transformation1Image,
            binding.transformation2Image,
            binding.transformation3Image
        )
        platformGifDecoders().forEachIndexed { index, factory ->
            transformationTexts[index].text = factory.toString()
            transformationImages[index].loadImage(ResourceImages.animGif.uri) {
                placeholder(
                    IconDrawableStateImage(
                        icon = R.drawable.ic_image_outline,
                        background = R.color.md_theme_primaryContainer,
                        iconTint = R.color.md_theme_onPrimaryContainer
                    )
                )
                error(
                    IconDrawableStateImage(
                        icon = R.drawable.ic_image_broken_outline,
                        background = R.color.md_theme_primaryContainer,
                        iconTint = R.color.md_theme_onPrimaryContainer
                    )
                )
                animatedTransformation(TestAnimatedTransformation)
                components {
                    add(factory)
                }
            }
        }
    }
}