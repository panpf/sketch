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

package com.github.panpf.sketch.sample.ui.gallery

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.panpf.sketch.loadImage
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.GridItemImageBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingItemFactory
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.sample.ui.model.photoSize
import com.github.panpf.sketch.util.Size
import com.github.panpf.tools4a.display.ktx.getScreenWidth
import kotlin.math.roundToInt

class PhotoGridItemFactory(val animatedPlaceholder: Boolean = false) :
    BaseBindingItemFactory<Photo, GridItemImageBinding>(Photo::class) {

    private var baseItemSize: Size? = null

    override fun createItemViewBinding(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ): GridItemImageBinding {
        if (baseItemSize == null && parent is RecyclerView) {
            val screenWidth = context.getScreenWidth()
            val gridDivider = context.resources.getDimensionPixelSize(R.dimen.grid_divider)
            baseItemSize = when (val layoutManager = parent.layoutManager) {
                is GridLayoutManager -> {
                    val spanCount = layoutManager.spanCount
                    val itemSize1 = (screenWidth - (gridDivider * (spanCount + 1))) / spanCount
                    Size(itemSize1, itemSize1)
                }

                is StaggeredGridLayoutManager -> {
                    val spanCount = layoutManager.spanCount
                    val itemSize1 = (screenWidth - (gridDivider * (spanCount + 1))) / spanCount
                    Size(itemSize1, -1)
                }

                else -> {
                    Size(screenWidth, -1)
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
        binding.myListImage.setAnimatedPlaceholder(animatedPlaceholder)
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
                val gridItemSize = computeGridItemSize(data.photoSize, baseItemSize!!)
                width = gridItemSize.width
                height = gridItemSize.height
            }
            loadImage(data.listThumbnailUrl)
        }
    }

    private fun computeGridItemSize(photoSize: Size?, baseItemSize: Size): Size {
        val width: Int
        val height: Int
        if (photoSize != null) {
            width = baseItemSize.width
            height = if (baseItemSize.height == -1) {
                val photoAspectRatio = photoSize.width.toFloat() / photoSize.height.toFloat()
                (baseItemSize.width / photoAspectRatio).roundToInt()
            } else {
                baseItemSize.height
            }
        } else {
            width = baseItemSize.width
            height = baseItemSize.width
        }
        return Size(width, height)
    }
}