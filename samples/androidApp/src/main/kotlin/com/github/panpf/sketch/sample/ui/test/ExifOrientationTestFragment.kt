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

package com.github.panpf.sketch.sample.ui.test

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.github.panpf.assemblyadapter.recycler.AssemblyRecyclerAdapter
import com.github.panpf.assemblyadapter.recycler.ItemSpan
import com.github.panpf.assemblyadapter.recycler.divider.Divider
import com.github.panpf.assemblyadapter.recycler.divider.addAssemblyGridDividerItemDecoration
import com.github.panpf.assemblyadapter.recycler.newAssemblyGridLayoutManager
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.FragmentRecyclerBinding
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.common.list.LoadStateItemFactory
import com.github.panpf.sketch.sample.ui.gallery.PhotoTestItemFactory
import com.github.panpf.sketch.sample.ui.model.PhotoTestItem
import com.github.panpf.sketch.sample.ui.util.parentViewModel
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.tools4k.lang.asOrThrow

class ExifOrientationTestFragment : BaseToolbarBindingFragment<FragmentRecyclerBinding>() {

    private val viewModel by parentViewModel<ExifOrientationTestViewModel>()

    override fun getNavigationBarInsetsView(binding: FragmentRecyclerBinding): View {
        return binding.root
    }

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: FragmentRecyclerBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "ExifOrientationTest"

        binding.recycler.apply {
            layoutManager =
                newAssemblyGridLayoutManager(3, GridLayoutManager.VERTICAL) {
                    itemSpanByItemFactory(
                        LoadStateItemFactory::class,
                        ItemSpan.fullSpan()
                    )
                }
            addAssemblyGridDividerItemDecoration {
                val gridDivider =
                    requireContext().resources.getDimensionPixelSize(R.dimen.grid_divider)
                divider(Divider.space(gridDivider))
                sideDivider(Divider.space(gridDivider))
                useDividerAsHeaderAndFooterDivider()
                useSideDividerAsSideHeaderAndFooterDivider()
            }

            adapter = AssemblyRecyclerAdapter<PhotoTestItem>(
                itemFactoryList = listOf(PhotoTestItemFactory()),
            )
        }

        viewModel.data.repeatCollectWithLifecycle(
            owner = viewLifecycleOwner,
            state = Lifecycle.State.CREATED
        ) { list ->
            binding.recycler.adapter!!
                .asOrThrow<AssemblyRecyclerAdapter<PhotoTestItem>>()
                .submitList(list)
        }
    }
}
