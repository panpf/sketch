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

import android.os.Bundle
import androidx.core.os.bundleOf
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.sample.databinding.ExifOrientationTestFragmentBinding
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import java.io.File

class ExifOrientationTestFragment :
    BindingFragment<ExifOrientationTestFragmentBinding>() {

    companion object {
        fun create(file: File): ExifOrientationTestFragment = ExifOrientationTestFragment().apply {
            arguments = bundleOf("filePath" to file.path)
        }
    }

    override fun onViewCreated(
        binding: ExifOrientationTestFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        val filePath = arguments?.getString("filePath")

        binding.exifOrientationTestImage.displayImage(filePath) {
            ignoreExifOrientation(true)
        }

        binding.exifOrientationTestImage2.displayImage(filePath) {
            ignoreExifOrientation(false)
        }
    }
}