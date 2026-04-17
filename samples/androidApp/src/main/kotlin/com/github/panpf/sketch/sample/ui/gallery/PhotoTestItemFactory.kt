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
import android.graphics.Point
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.panpf.sketch.ability.showDataFromLogo
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.loadImage
import com.github.panpf.sketch.request.updateImageOptions
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.GridItemTestPhotoBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingItemFactory
import com.github.panpf.sketch.sample.ui.model.PhotoTestItem
import com.github.panpf.sketch.state.IconDrawableStateImage
import com.github.panpf.tools4a.display.ktx.getScreenWidth

class PhotoTestItemFactory :
    BaseBindingItemFactory<PhotoTestItem, GridItemTestPhotoBinding>(PhotoTestItem::class) {

    private var itemSize: Point? = null

    override fun createItemViewBinding(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ): GridItemTestPhotoBinding {
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
        binding: GridItemTestPhotoBinding,
        item: BindingItem<PhotoTestItem, GridItemTestPhotoBinding>
    ) {
        binding.myListImage.apply {
            updateImageOptions {
                placeholder(
                    IconDrawableStateImage(
                        icon = R.drawable.ic_image_outline,
                        background = R.color.placeholder_bg,
                    )
                )
                error(
                    IconDrawableStateImage(
                        icon = R.drawable.ic_image_broken_outline,
                        background = R.color.placeholder_bg,
                    )
                )
                crossfade()
                resizeOnDraw()
                sizeMultiplier(2f)  // To get a clearer thumbnail
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
            }
            showDataFromLogo()
        }
    }

    override fun bindItemData(
        context: Context,
        binding: GridItemTestPhotoBinding,
        item: BindingItem<PhotoTestItem, GridItemTestPhotoBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: PhotoTestItem
    ) {
        binding.root.updateLayoutParams<LayoutParams> {
            val itemSize = itemSize!!
            width = itemSize.x
            height = itemSize.x
        }
        binding.myListImage.updateLayoutParams<LayoutParams> {
            val itemSize = itemSize!!
            width = itemSize.x
            height = itemSize.x
        }

        binding.myListImage.loadImage(data.photoUri) {
            val imageDecoder = data.imageDecoder
            if (imageDecoder != null) {
                components {
                    add(imageDecoder)
                }
            }
            val fetchFactory = data.imageFetcher
            if (fetchFactory != null) {
                components {
                    add(fetchFactory)
                }
            }
        }
        val title = if (data.apiSupport) {
            data.title
        } else if (data.title?.isNotEmpty() == true) {
            "${data.title} (API Level Not Supported)"
        } else {
            "API Level Not Supported"
        }
        binding.text.text = title
        binding.text.isVisible = title != null
    }
}