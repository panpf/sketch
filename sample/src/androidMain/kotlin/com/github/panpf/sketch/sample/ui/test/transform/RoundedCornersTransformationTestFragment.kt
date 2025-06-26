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
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.SeekBar
import androidx.lifecycle.Lifecycle.State
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.loadImage
import com.github.panpf.sketch.request.colorType
import com.github.panpf.sketch.sample.databinding.FragmentTestTransformationRoundedCornersBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import org.koin.androidx.viewmodel.ext.android.viewModel

class RoundedCornersTransformationTestFragment :
    BaseBindingFragment<FragmentTestTransformationRoundedCornersBinding>() {

    private val roundedCornersTransformationTestViewModel by viewModel<RoundedCornersTransformationTestViewModel>()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(
        binding: FragmentTestTransformationRoundedCornersBinding,
        savedInstanceState: Bundle?
    ) {
        roundedCornersTransformationTestViewModel.topLeftRadiusData.repeatCollectWithLifecycle(
            viewLifecycleOwner,
            State.CREATED
        ) {
            updateImage(binding)
            binding.topLeftRadiusText.text = "$it"
        }
        binding.topLeftSeekBar.apply {
            max = 100
            progress = roundedCornersTransformationTestViewModel.topLeftRadiusData.value
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    roundedCornersTransformationTestViewModel.changeTopLeftRadius(progress)
                }
            })
        }

        roundedCornersTransformationTestViewModel.topRightRadiusData.repeatCollectWithLifecycle(
            viewLifecycleOwner,
            State.CREATED
        ) {
            updateImage(binding)
            binding.topRightRadiusText.text = "$it"
        }
        binding.topRightSeekBar.apply {
            max = 100
            progress = roundedCornersTransformationTestViewModel.topRightRadiusData.value
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    roundedCornersTransformationTestViewModel.changeTopRightRadius(progress)
                }
            })
        }

        roundedCornersTransformationTestViewModel.bottomLeftRadiusData
            .repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) {
            updateImage(binding)
            binding.bottomLeftRadiusText.text = "$it"
        }
        binding.bottomLeftSeekBar.apply {
            max = 100
            progress = roundedCornersTransformationTestViewModel.bottomLeftRadiusData.value
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    roundedCornersTransformationTestViewModel.changeBottomLeftRadius(progress)
                }
            })
        }

        roundedCornersTransformationTestViewModel.bottomRightRadiusData
            .repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) {
            updateImage(binding)
            binding.bottomRightRadiusText.text = "$it"
        }
        binding.bottomRightSeekBar.apply {
            max = 100
            progress = roundedCornersTransformationTestViewModel.bottomRightRadiusData.value
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    roundedCornersTransformationTestViewModel.changeBottomRightRadius(progress)
                }
            })
        }
    }

    private fun updateImage(binding: FragmentTestTransformationRoundedCornersBinding) {
        binding.myImage.loadImage(ResourceImages.statics.first().uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            addTransformations(
                RoundedCornersTransformation(
                    topLeft = roundedCornersTransformationTestViewModel.topLeftRadiusData.value.toFloat(),
                    topRight = roundedCornersTransformationTestViewModel.topRightRadiusData.value.toFloat(),
                    bottomLeft = roundedCornersTransformationTestViewModel.bottomLeftRadiusData.value.toFloat(),
                    bottomRight = roundedCornersTransformationTestViewModel.bottomRightRadiusData.value.toFloat(),
                )
            )
            colorType(Bitmap.Config.RGB_565) // To test automatic conversion Config
        }
    }
}