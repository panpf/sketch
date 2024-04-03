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

import android.app.Application
import android.os.Bundle
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.images.MyImages
import com.github.panpf.sketch.sample.databinding.FragmentTestTransformationRotateBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.transform.RotateTransformation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RotateTransformationTestFragment :
    BaseBindingFragment<FragmentTestTransformationRotateBinding>() {

    private val viewModel by viewModels<RotateTransformationTestViewModel>()

    override fun onViewCreated(
        binding: FragmentTestTransformationRotateBinding,
        savedInstanceState: Bundle?
    ) {
        viewModel.rotateData.repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
            binding.myImage.displayImage(MyImages.statics.first().uri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                addTransformations(RotateTransformation(it))
            }

            binding.degreesText.text = "$it"
        }

        binding.degreesSeekBar.apply {
            max = 360
            progress = viewModel.rotateData.value
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                ) {
                    viewModel.changeRotate(progress.coerceAtLeast(0))
                }
            })
        }
    }

    class RotateTransformationTestViewModel(application1: Application) :
        LifecycleAndroidViewModel(application1) {

        private val _rotateData = MutableStateFlow(45)
        val rotateData: StateFlow<Int> = _rotateData

        fun changeRotate(rotate: Int) {
            _rotateData.value = rotate
        }
    }
}