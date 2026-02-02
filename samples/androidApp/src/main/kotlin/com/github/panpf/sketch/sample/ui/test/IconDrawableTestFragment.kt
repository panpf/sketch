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

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.github.panpf.sketch.drawable.IconDrawable
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.FragmentTestIconDrawableBinding
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.util.getDrawableCompat

class IconDrawableTestFragment : BaseToolbarBindingFragment<FragmentTestIconDrawableBinding>() {

    override fun getNavigationBarInsetsView(binding: FragmentTestIconDrawableBinding): View {
        return binding.root
    }

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: FragmentTestIconDrawableBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "IconDrawable"

        val resources = binding.root.resources
        binding.image1.setImageDrawable(
            IconDrawable(
                icon = resources.getDrawableCompat(R.drawable.ic_image_outline),
                background = ColorDrawable(Color.GREEN)
            )
        )
        binding.image2.setImageDrawable(
            IconDrawable(
                icon = resources.getDrawableCompat(R.drawable.ic_image_outline_big),
                background = ColorDrawable(Color.GREEN)
            )
        )
        binding.image3.setImageDrawable(
            IconDrawable(
                icon = resources.getDrawableCompat(R.drawable.ic_image_outline),
                background = resources.getDrawableCompat(com.github.panpf.sketch.images.R.drawable.play)
            )
        )
    }
}