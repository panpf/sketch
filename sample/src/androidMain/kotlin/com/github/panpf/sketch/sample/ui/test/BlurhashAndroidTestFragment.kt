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

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.fetch.newBlurhashUri
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.loadImage
import com.github.panpf.sketch.sample.databinding.FragmentTestBlurhashAndroidBinding
import com.github.panpf.sketch.sample.image.DelayDecodeInterceptor
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.state.BlurhashStateImage

class BlurhashAndroidTestFragment : BaseToolbarBindingFragment<FragmentTestBlurhashAndroidBinding>() {

    override fun getNavigationBarInsetsView(binding: FragmentTestBlurhashAndroidBinding): View {
        return binding.root
    }

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: FragmentTestBlurhashAndroidBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Blurhash Android View"

        binding.myImage1.loadImage(ResourceImages.jpeg.uri) {
            memoryCachePolicy(DISABLED)
            resultCachePolicy(DISABLED)
            placeholder(BlurhashStateImage("d7D+0q5W00^h01~A~B0gInR%?G9vR%R+NH=_I;NG\$\$-o"))
            components {
                addDecodeInterceptor(DelayDecodeInterceptor(3000))
            }
        }

        // Example blurhash strings
        val blurhash1 = "L6PZfSi_.AyE_3t7t7R**0o#DgR4"
        val blurhash2 = "UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2"
        val blurhash3 = "L9HL7nxu00WB~qj[ayfQ00WB~qj["
        val blurhash4 = "LGF5]+Yk^6#M@-5c,1J5@[or[Q6."
        val blurhash5 = "L6Pj0^jE.AyE_3t7t7R**0o#DgR4"

        // Blurhash URI with SketchImageView
        binding.imageView1.loadImage(newBlurhashUri(blurhash1))

        // Blurhash as placeholder
        binding.imageView2.loadImage("https://httpbin.org/delay/3") {
            placeholder(com.github.panpf.sketch.state.BlurhashStateImage(blurhash2))
        }

        // Blurhash as error state
        binding.imageView3.loadImage("invalid_url") {
            error(com.github.panpf.sketch.state.BlurhashStateImage(blurhash3))
        }

        // Different blurhash variations
        binding.imageView4.loadImage(newBlurhashUri(blurhash1))
        binding.imageView5.loadImage(newBlurhashUri(blurhash2))
        binding.imageView6.loadImage(newBlurhashUri(blurhash4))
        binding.imageView7.loadImage(newBlurhashUri(blurhash5))
    }
}