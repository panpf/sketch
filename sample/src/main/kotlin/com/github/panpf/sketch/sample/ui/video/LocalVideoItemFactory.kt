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
package com.github.panpf.sketch.sample.ui.video

import android.content.Context
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.request.updateDisplayImageOptions
import com.github.panpf.sketch.request.videoFramePercent
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.databinding.ListItemVideoBinding
import com.github.panpf.sketch.sample.model.VideoInfo
import com.github.panpf.sketch.sample.ui.base.BaseBindingItemFactory
import com.github.panpf.sketch.stateimage.IconStateImage
import com.github.panpf.sketch.stateimage.saveCellularTrafficError

class LocalVideoItemFactory :
    BaseBindingItemFactory<VideoInfo, ListItemVideoBinding>(VideoInfo::class) {

    override fun initItem(
        context: Context,
        binding: ListItemVideoBinding,
        item: BindingItem<VideoInfo, ListItemVideoBinding>
    ) {
        binding.thumbnailImage.updateDisplayImageOptions {
            placeholder(IconStateImage(R.drawable.ic_image_outline) {
                resColorBackground(R.color.placeholder_bg)
            })
            error(IconStateImage(R.drawable.ic_error_baseline) {
                resColorBackground(R.color.placeholder_bg)
            }) {
                saveCellularTrafficError(IconStateImage(R.drawable.im_save_cellular_traffic) {
                    resColorBackground(R.color.placeholder_bg)
                })
            }
            crossfade()
            videoFramePercent(0.5f)
        }
    }

    override fun bindItemData(
        context: Context,
        binding: ListItemVideoBinding,
        item: BindingItem<VideoInfo, ListItemVideoBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: VideoInfo
    ) {
        binding.thumbnailImage.displayImage(data.path) {
            merge(context.appSettingsService.buildListImageOptions())
        }
        binding.nameText.text = data.title
        binding.sizeText.text = data.getTempFormattedSize(context)
        binding.dateText.text = data.tempFormattedDate
        binding.durationText.text = data.tempFormattedDuration
    }
}
