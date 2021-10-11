/*
 * Copyright (C) 2021 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.panpf.sketch.sample.item

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.panpf.assemblyadapter.BindingItemFactory
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.bean.AppsOverview
import me.panpf.sketch.sample.databinding.ItemAppsOverviewBinding

class AppsOverviewItemFactory :
    BindingItemFactory<AppsOverview, ItemAppsOverviewBinding>(AppsOverview::class) {

    override fun createItemViewBinding(
        context: Context, inflater: LayoutInflater, parent: ViewGroup
    ): ItemAppsOverviewBinding {
        return ItemAppsOverviewBinding.inflate(inflater, parent, false)
    }

    override fun initItem(
        context: Context,
        binding: ItemAppsOverviewBinding,
        item: BindingItem<AppsOverview, ItemAppsOverviewBinding>
    ) {
    }

    override fun bindItemData(
        context: Context,
        binding: ItemAppsOverviewBinding,
        item: BindingItem<AppsOverview, ItemAppsOverviewBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: AppsOverview
    ) {
        binding.appsOverviewItemContentText.text = context.getString(
            R.string.apps_overview_item, data.count, data.userAppCount, data.groupCount
        )
    }
}
