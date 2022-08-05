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
package com.github.panpf.sketch.sample.ui.viewer

import android.os.Bundle
import androidx.navigation.navArgs
import com.github.panpf.sketch.sample.databinding.FragmentContainerActivityBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingActivity

/**
 * Because the sliding back function needs to reveal the content of the previous page when sliding.
 * The Navigation component currently cannot implement add Fragment.
 * So it can only be implemented with Activity.
 */
class ImageViewerActivity : BaseBindingActivity<FragmentContainerActivityBinding>() {
    private val args by navArgs<ImageViewerActivityArgs>()

    override fun onCreate(binding: FragmentContainerActivityBinding, savedInstanceState: Bundle?) {
        val newArgs = ImageViewerPagerFragmentArgs(
            args.imageDetailJsonArray,
            args.defaultPosition
        )
        supportFragmentManager.beginTransaction()
            .replace(
                binding.fragmentContainerActivityContainerView.id,
                ImageViewerPagerFragment().apply {
                    arguments = newArgs.toBundle()
                })
            .commit()
    }
}