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

package com.github.panpf.sketch.sample.ui.test.transform

import android.graphics.Color
import android.os.Bundle
import android.widget.SeekBar
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.Lifecycle.State
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.loadImage
import com.github.panpf.sketch.sample.databinding.FragmentTestTransformationBlurBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.transform.BlurTransformation
import org.koin.androidx.viewmodel.ext.android.viewModel

class BlurTransformationTestFragment :
    BaseBindingFragment<FragmentTestTransformationBlurBinding>() {

    private val testViewModel by viewModel<BlurTransformationTestViewModel>()

    override fun onViewCreated(
        binding: FragmentTestTransformationBlurBinding,
        savedInstanceState: Bundle?
    ) {
        testViewModel.radiusData.repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) {
            updateImage(binding = binding)
            binding.valueText.text = "$it"
        }
        binding.seekBar.apply {
            max = 100
            progress = testViewModel.radiusData.value
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    testViewModel.changeRadius(progress.coerceAtLeast(1))
                }
            })
        }

        testViewModel.maskColorData.repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) {
            updateImage(binding = binding)
        }
        binding.noneButton1.isChecked = true
        binding.noneButton1.setOnClickListener {
            testViewModel.changeMaskColor(null)
        }
        binding.redButton.setOnClickListener {
            testViewModel.changeMaskColor(ColorUtils.setAlphaComponent(Color.RED, 128))
        }
        binding.greenButton.setOnClickListener {
            testViewModel.changeMaskColor(ColorUtils.setAlphaComponent(Color.GREEN, 128))
        }
        binding.blueButton.setOnClickListener {
            testViewModel.changeMaskColor(ColorUtils.setAlphaComponent(Color.BLUE, 128))
        }

        testViewModel.backgroundColorData
            .repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) {
                updateImage(binding = binding)
            }
        binding.noneButton2.isChecked = true
        binding.noneButton2.setOnClickListener {
            testViewModel.changeBackgroundColor(null)
        }
        binding.blackButton.setOnClickListener {
            testViewModel.changeBackgroundColor(Color.BLACK)
        }
        binding.whiteButton.setOnClickListener {
            testViewModel.changeBackgroundColor(Color.WHITE)
        }
    }

    private fun updateImage(binding: FragmentTestTransformationBlurBinding) {
        val radius = testViewModel.radiusData.value
        val maskColor = testViewModel.maskColorData.value
        val backgroundColor = testViewModel.backgroundColorData.value

        binding.myImage1.loadImage(ComposeResImageFiles.jpeg.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            addTransformations(
                BlurTransformation(
                    radius = radius,
                    maskColor = maskColor,
                    hasAlphaBitmapBgColor = backgroundColor
                )
            )
        }

        binding.myImage2.loadImage(ComposeResImageFiles.png.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            addTransformations(
                BlurTransformation(
                    radius = radius,
                    maskColor = maskColor,
                    hasAlphaBitmapBgColor = backgroundColor
                )
            )
        }
    }
}