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
package com.github.panpf.sketch.sample.ui.photo

import android.content.Context
import android.graphics.Point
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.request.updateDisplayImageOptions
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.ImageGridItemBinding
import com.github.panpf.sketch.sample.model.Photo
import com.github.panpf.sketch.sample.prefsService
import com.github.panpf.sketch.sample.ui.common.list.MyBindingItemFactory
import com.github.panpf.sketch.stateimage.IconStateImage
import com.github.panpf.sketch.stateimage.ResColor
import com.github.panpf.sketch.stateimage.saveCellularTrafficError
import com.github.panpf.sketch.viewability.setClickIgnoreSaveCellularTrafficEnabled
import com.github.panpf.tools4a.display.ktx.getScreenWidth
import kotlin.math.roundToInt

class ImageGridItemFactory(val animatedPlaceholder: Boolean = false) :
    MyBindingItemFactory<Photo, ImageGridItemBinding>(Photo::class) {

    private var itemSize: Point? = null

    override fun createItemViewBinding(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ImageGridItemBinding {
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
        binding: ImageGridItemBinding,
        item: BindingItem<Photo, ImageGridItemBinding>
    ) {

        binding.imageGridItemImage.apply {
            setClickIgnoreSaveCellularTrafficEnabled(true)
            updateDisplayImageOptions {
                if (animatedPlaceholder) {
                    placeholder(R.drawable.ic_placeholder_eclipse_animated)
                } else {
                    placeholder(
                        IconStateImage(
                            R.drawable.ic_image_outline,
                            ResColor(R.color.placeholder_bg)
                        )
                    )
                }
                error(IconStateImage(R.drawable.ic_error, ResColor(R.color.placeholder_bg))) {
                    saveCellularTrafficError(
                        IconStateImage(
                            R.drawable.ic_signal_cellular,
                            ResColor(R.color.placeholder_bg)
                        )
                    )
                }
                crossfade()
                resizeApplyToDrawable()
            }
        }
    }

    override fun bindItemData(
        context: Context,
        binding: ImageGridItemBinding,
        item: BindingItem<Photo, ImageGridItemBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: Photo
    ) {
        binding.imageGridItemImage.apply {
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

            displayImage(data.listThumbnailUrl) {
                merge(prefsService.buildListImageOptions())
            }
        }
    }
}