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

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.widget.SeekBar
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.Lifecycle.State
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.loadImage
import com.github.panpf.sketch.sample.databinding.FragmentTestTransformationMultiBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.transform.BlurTransformation
import com.github.panpf.sketch.transform.MaskTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import org.koin.androidx.viewmodel.ext.android.viewModel

class MultiTransformationTestFragment :
    BaseBindingFragment<FragmentTestTransformationMultiBinding>() {

    private val multiTransformationTestViewModel by viewModel<MultiTransformationTestViewModel>()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(
        binding: FragmentTestTransformationMultiBinding,
        savedInstanceState: Bundle?
    ) {
        multiTransformationTestViewModel.rotateData.repeatCollectWithLifecycle(
            viewLifecycleOwner,
            State.CREATED
        ) {
            updateImage(binding)
            binding.degreesText.text = "$it"
        }
        binding.degreesSeekBar.apply {
            max = 360
            progress = multiTransformationTestViewModel.rotateData.value
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    multiTransformationTestViewModel.changeRotate(progress.coerceAtLeast(0))
                }
            })
        }

        multiTransformationTestViewModel.blurRadiusData.repeatCollectWithLifecycle(
            viewLifecycleOwner,
            State.CREATED
        ) {
            updateImage(binding)
            binding.blurValueText.text = "$it"
        }
        binding.blurSeekBar.apply {
            max = 100
            progress = multiTransformationTestViewModel.blurRadiusData.value
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    multiTransformationTestViewModel.changeBlurRadius(progress.coerceAtLeast(1))
                }
            })
        }

        multiTransformationTestViewModel.roundedCornersRadiusData
            .repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) {
                updateImage(binding)
                binding.roundedCornersRadiusText.text = "$it"
            }
        binding.roundedCornersSeekBar.apply {
            max = 100
            progress = multiTransformationTestViewModel.roundedCornersRadiusData.value
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    multiTransformationTestViewModel.changeRoundedCornersRadius(progress)
                }
            })
        }

        multiTransformationTestViewModel.maskColorData.repeatCollectWithLifecycle(
            viewLifecycleOwner,
            State.CREATED
        ) {
            updateImage(binding)
        }
        binding.redButton.isChecked = true
        binding.noneButton1.setOnClickListener {
            multiTransformationTestViewModel.changeMaskColor(null)
        }
        binding.redButton.setOnClickListener {
            multiTransformationTestViewModel.changeMaskColor(
                ColorUtils.setAlphaComponent(
                    Color.RED,
                    128
                )
            )
        }
        binding.greenButton.setOnClickListener {
            multiTransformationTestViewModel.changeMaskColor(
                ColorUtils.setAlphaComponent(
                    Color.GREEN,
                    128
                )
            )
        }
        binding.blueButton.setOnClickListener {
            multiTransformationTestViewModel.changeMaskColor(
                ColorUtils.setAlphaComponent(
                    Color.BLUE,
                    128
                )
            )
        }
    }

    private fun updateImage(binding: FragmentTestTransformationMultiBinding) {
        binding.myImage.loadImage(ComposeResImageFiles.statics.first().uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            addTransformations(
                BlurTransformation(radius = multiTransformationTestViewModel.blurRadiusData.value),
                RoundedCornersTransformation(multiTransformationTestViewModel.roundedCornersRadiusData.value.toFloat()),
                MaskTransformation(
                    maskColor = multiTransformationTestViewModel.maskColorData.value
                        ?: Color.TRANSPARENT
                ),
                RotateTransformation(multiTransformationTestViewModel.rotateData.value),
            )
        }
    }
}