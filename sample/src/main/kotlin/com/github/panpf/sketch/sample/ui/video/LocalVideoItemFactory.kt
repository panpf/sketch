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
import com.github.panpf.sketch.sample.databinding.VideoItemBinding
import com.github.panpf.sketch.sample.model.VideoInfo
import com.github.panpf.sketch.sample.ui.common.list.MyBindingItemFactory
import com.github.panpf.sketch.stateimage.IconStateImage
import com.github.panpf.sketch.stateimage.ResColor
import com.github.panpf.sketch.stateimage.saveCellularTrafficError

class LocalVideoItemFactory :
    MyBindingItemFactory<VideoInfo, VideoItemBinding>(VideoInfo::class) {

    override fun initItem(
        context: Context,
        binding: VideoItemBinding,
        item: BindingItem<VideoInfo, VideoItemBinding>
    ) {
        binding.videoItemIconImage.updateDisplayImageOptions {
            val bg = ResColor(R.color.placeholder_bg)
            placeholder(IconStateImage(R.drawable.ic_image_outline, bg))
            error(IconStateImage(R.drawable.ic_error, bg)) {
                saveCellularTrafficError(R.drawable.im_save_cellular_traffic)
            }
            crossfade()
            videoFramePercent(0.5f)
        }
    }

    override fun bindItemData(
        context: Context,
        binding: VideoItemBinding,
        item: BindingItem<VideoInfo, VideoItemBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: VideoInfo
    ) {
        binding.videoItemIconImage.displayImage(data.path) {
            merge(context.appSettingsService.buildListImageOptions())
        }
        binding.videoItemNameText.text = data.title
        binding.videoItemSizeText.text = data.getTempFormattedSize(context)
        binding.videoItemDateText.text = data.tempFormattedDate
        binding.videoItemDurationText.text = data.tempFormattedDuration
    }
}
