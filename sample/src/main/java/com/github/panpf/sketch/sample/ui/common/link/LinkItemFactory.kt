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
package com.github.panpf.sketch.sample.ui.common.link

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.navigation.findNavController
import com.github.panpf.sketch.sample.databinding.LinkItemBinding
import com.github.panpf.sketch.sample.model.Link
import com.github.panpf.sketch.sample.ui.common.list.MyBindingItemFactory

class LinkItemFactory : MyBindingItemFactory<Link, LinkItemBinding>(Link::class) {

    override fun initItem(
        context: Context,
        binding: LinkItemBinding,
        item: BindingItem<Link, LinkItemBinding>
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
        binding: LinkItemBinding,
        item: BindingItem<Link, LinkItemBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: Link
    ) {
        binding.linkItemTitleText.text = data.title
    }
}