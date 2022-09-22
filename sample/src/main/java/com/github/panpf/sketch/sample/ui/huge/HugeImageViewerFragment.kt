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
package com.github.panpf.sketch.sample.ui.huge

import android.os.Bundle
import android.widget.ImageView.ScaleType
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.github.panpf.assemblyadapter.pager.FragmentItemFactory
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.sample.databinding.HugeImageViewerFragmentBinding
import com.github.panpf.sketch.sample.eventService
import com.github.panpf.sketch.sample.prefsService
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.github.panpf.sketch.sample.ui.setting.ImageInfoDialogFragment
import com.github.panpf.sketch.sample.util.observeWithFragmentView
import com.github.panpf.sketch.viewability.showRingProgressIndicator

class HugeImageViewerFragment : BindingFragment<HugeImageViewerFragmentBinding>() {

    private val args by navArgs<HugeImageViewerFragmentArgs>()

    override fun onViewCreated(
        binding: HugeImageViewerFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        binding.hugeImageViewerZoomImage.apply {
            showRingProgressIndicator()

            prefsService.scrollBarEnabled.stateFlow.observeWithFragmentView(this@HugeImageViewerFragment) {
                scrollBarEnabled = it
            }
            prefsService.readModeEnabled.stateFlow.observeWithFragmentView(this@HugeImageViewerFragment) {
                readModeEnabled = it
            }
            prefsService.showTileBoundsInHugeImagePage.stateFlow.observeWithFragmentView(this@HugeImageViewerFragment) {
                showTileBounds = it
            }
            prefsService.scaleType.stateFlow.observeWithFragmentView(this@HugeImageViewerFragment) {
                scaleType = ScaleType.valueOf(it)
            }

            eventService.hugeViewerPageRotateEvent.listen(viewLifecycleOwner) {
                if (isResumed) {
                    rotateBy(90)
                }
            }

            setOnLongClickListener {
                findNavController().navigate(
                    ImageInfoDialogFragment.createDirectionsFromImageView(this, null)
                )
                true
            }

            displayImage(args.imageUri) {
                lifecycle(viewLifecycleOwner.lifecycle)
            }
        }

        binding.hugeImageViewerTileMap.apply {
            setZoomImageView(binding.hugeImageViewerZoomImage)
            displayImage(args.imageUri)
        }
    }

    class ItemFactory : FragmentItemFactory<String>(String::class) {

        override fun createFragment(
            bindingAdapterPosition: Int,
            absoluteAdapterPosition: Int,
            data: String
        ): Fragment = HugeImageViewerFragment().apply {
            arguments = HugeImageViewerFragmentArgs(data).toBundle()
        }
    }
}