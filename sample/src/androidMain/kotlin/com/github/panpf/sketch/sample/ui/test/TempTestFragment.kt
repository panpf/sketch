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

import android.graphics.ImageDecoder
import android.graphics.drawable.Animatable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.decodeDrawable
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.sample.databinding.FragmentTestTempBinding
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import pl.droidsonroids.gif.GifDrawable

class TempTestFragment : BaseToolbarBindingFragment<FragmentTestTempBinding>() {

    override fun getStatusBarInsetsView(binding: FragmentTestTempBinding): View {
        return binding.root
    }

    override fun getNavigationBarInsetsView(binding: FragmentTestTempBinding): View {
        return binding.root
    }

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: FragmentTestTempBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Temp"

        val drawable = GifDrawable(requireContext().assets, ResourceImages.animGif.resourceName)
        binding.myImage.setImageDrawable(drawable)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val imageSource = ImageDecoder.createSource(
                requireContext().assets,
                ResourceImages.animGif.resourceName
            )
            val drawable2 = imageSource.decodeDrawable { _, _ -> }
            (drawable2 as Animatable).start()
            binding.myImage2.setImageDrawable(drawable2)
        }

//        binding.myImage.apply {
//            loadImage(ResourceImages.jpeg.uri) {
//                memoryCachePolicy(DISABLED)
//                resultCachePolicy(DISABLED)
//            }
//        }
    }
}