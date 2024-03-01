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
package com.github.panpf.sketch.sample.ui.test.transform

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.sample.databinding.FragmentTestTransformationRoundedCornersBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.transform.RoundedCornersTransformation

class RoundedCornersTransformationTestFragment :
    BaseBindingFragment<FragmentTestTransformationRoundedCornersBinding>() {

    private val viewModel by viewModels<RoundedCornersTransformationTestViewModel>()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(
        binding: FragmentTestTransformationRoundedCornersBinding,
        savedInstanceState: Bundle?
    ) {
        viewModel.topLeftRadiusData.repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
            updateImage(binding)
            binding.topLeftRadiusText.text = "$it"
        }
        binding.topLeftSeekBar.apply {
            max = 100
            progress = viewModel.topLeftRadiusData.value
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    viewModel.changeTopLeftRadius(progress)
                }
            })
        }

        viewModel.topRightRadiusData.repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
            updateImage(binding)
            binding.topRightRadiusText.text = "$it"
        }
        binding.topRightSeekBar.apply {
            max = 100
            progress = viewModel.topRightRadiusData.value
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    viewModel.changeTopRightRadius(progress)
                }
            })
        }

        viewModel.bottomLeftRadiusData.repeatCollectWithLifecycle(
            viewLifecycleOwner,
            State.STARTED
        ) {
            updateImage(binding)
            binding.bottomLeftRadiusText.text = "$it"
        }
        binding.bottomLeftSeekBar.apply {
            max = 100
            progress = viewModel.bottomLeftRadiusData.value
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    viewModel.changeBottomLeftRadius(progress)
                }
            })
        }

        viewModel.bottomRightRadiusData.repeatCollectWithLifecycle(
            viewLifecycleOwner,
            State.STARTED
        ) {
            updateImage(binding)
            binding.bottomRightRadiusText.text = "$it"
        }
        binding.bottomRightSeekBar.apply {
            max = 100
            progress = viewModel.bottomRightRadiusData.value
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    viewModel.changeBottomRightRadius(progress)
                }
            })
        }
    }

    private fun updateImage(binding: FragmentTestTransformationRoundedCornersBinding) {
        binding.myImage.displayImage(AssetImages.statics.first().uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            addTransformations(
                RoundedCornersTransformation(
                    topLeft = viewModel.topLeftRadiusData.value.toFloat(),
                    topRight = viewModel.topRightRadiusData.value.toFloat(),
                    bottomLeft = viewModel.bottomLeftRadiusData.value.toFloat(),
                    bottomRight = viewModel.bottomRightRadiusData.value.toFloat(),
                )
            )
        }
    }
}