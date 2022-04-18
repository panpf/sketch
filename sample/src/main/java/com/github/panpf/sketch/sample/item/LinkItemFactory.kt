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
package com.github.panpf.sketch.sample.item

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.github.panpf.assemblyadapter.BindingItemFactory
import com.github.panpf.sketch.sample.bean.Link
import com.github.panpf.sketch.sample.databinding.ItemLinkBinding

class LinkItemFactory : BindingItemFactory<Link, ItemLinkBinding>(Link::class) {

    override fun createItemViewBinding(
        context: Context, inflater: LayoutInflater, parent: ViewGroup
    ): ItemLinkBinding {
        return ItemLinkBinding.inflate(inflater, parent, false)
    }

    override fun initItem(
        context: Context,
        binding: ItemLinkBinding,
        item: BindingItem<Link, ItemLinkBinding>
    ) {
        binding.root.setOnClickListener {
            val data = item.dataOrThrow
            if (data.minSdk == null || Build.VERSION.SDK_INT >= data.minSdk) {
                it.findNavController().navigate(data.navDirections)
            } else {
                Toast.makeText(
                    context,
                    "Must be API ${data.minSdk} or above",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun bindItemData(
        context: Context,
        binding: ItemLinkBinding,
        item: BindingItem<Link, ItemLinkBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: Link
    ) {
        binding.linkItemTitleText.text = data.title
    }
}