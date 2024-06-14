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
package com.github.panpf.sketch.sample.ui.gallery

import android.content.Context
import android.graphics.Point
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.panpf.sketch.ability.setClickIgnoreSaveCellularTrafficEnabled
import com.github.panpf.sketch.loadImage
import com.github.panpf.sketch.request.updateImageOptions
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.databinding.GridItemImageBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingItemFactory
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.state.IconAnimatableDrawableStateImage
import com.github.panpf.sketch.state.IconDrawableStateImage
import com.github.panpf.sketch.state.saveCellularTrafficError
import com.github.panpf.tools4a.display.ktx.getScreenWidth
import kotlin.math.roundToInt

class PhotoGridItemFactory constructor(val animatedPlaceholder: Boolean = false) :
    BaseBindingItemFactory<Photo, GridItemImageBinding>(Photo::class) {

    private var itemSize: Point? = null

    override fun createItemViewBinding(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ): GridItemImageBinding {
        if (itemSize == null && parent is RecyclerView) {
            val screenWidth = context.getScreenWidth()
            val gridDivider = context.resources.getDimensionPixelSize(R.dimen.grid_divider)
            itemSize = when (val layoutManager = parent.layoutManager) {
                is GridLayoutManager -> {
                    val spanCount = layoutManager.spanCount
                    val itemSize1 = (screenWidth - (gridDivider * (spanCount + 1))) / spanCount
                    Point(itemSize1, itemSize1)
                }

                is StaggeredGridLayoutManager -> {
                    val spanCount = layoutManager.spanCount
                    val itemSize1 = (screenWidth - (gridDivider * (spanCount + 1))) / spanCount
                    Point(itemSize1, -1)
                }

                else -> {
                    Point(screenWidth, -1)
                }
            }
        }
        return super.createItemViewBinding(context, inflater, parent)
    }

    override fun initItem(
        context: Context,
        binding: GridItemImageBinding,
        item: BindingItem<Photo, GridItemImageBinding>
    ) {
        binding.myListImage.apply {
            setClickIgnoreSaveCellularTrafficEnabled(true)
            updateImageOptions {
                if (animatedPlaceholder) {
                    placeholder(
                        IconAnimatableDrawableStateImage(
                            icon = R.drawable.ic_placeholder_eclipse_animated,
                            background = R.color.placeholder_bg
                        )
                    )
                } else {
                    placeholder(
                        IconDrawableStateImage(
                            icon = R.drawable.ic_image_outline,
                            background = R.color.placeholder_bg,
                        )
                    )
                }
                error(
                    defaultImage = IconDrawableStateImage(
                        icon = R.drawable.ic_error_baseline,
                        background = R.color.placeholder_bg
                    )
                ) {
                    saveCellularTrafficError(
                        IconDrawableStateImage(
                            icon = R.drawable.ic_signal_cellular,
                            background = R.color.placeholder_bg
                        )
                    )
                }
                crossfade()
                resizeOnDraw()
                sizeMultiplier(2f)  // To get a clearer thumbnail
            }
        }
    }

    override fun bindItemData(
        context: Context,
        binding: GridItemImageBinding,
        item: BindingItem<Photo, GridItemImageBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: Photo
    ) {
        binding.myListImage.apply {
            updateLayoutParams<LayoutParams> {
                val photoWidth = data.width
                val photoHeight = data.height
                val itemSize = itemSize!!
                if (photoWidth != null && photoHeight != null) {
                    width = itemSize.x
                    height = if (itemSize.y == -1) {
                        val previewAspectRatio = photoWidth.toFloat() / photoHeight.toFloat()
                        (itemSize.x / previewAspectRatio).roundToInt()
                    } else {
                        itemSize.y
                    }
                } else {
                    width = itemSize.x
                    height = itemSize.x
                }
            }

            loadImage(data.listThumbnailUrl) {
                merge(appSettingsService.buildListImageOptions())
            }
        }
    }
}