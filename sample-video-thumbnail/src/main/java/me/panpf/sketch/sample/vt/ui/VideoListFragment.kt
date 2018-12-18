/*
 * Copyright (C) 2013 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.sample.vt.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.setPadding
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_recycler.*
import me.panpf.adapter.paged.AssemblyPagedListAdapter
import me.panpf.androidxkt.arch.bindViewModel
import me.panpf.androidxkt.util.dp2px
import me.panpf.androidxkt.widget.showLongToast
import me.panpf.sketch.sample.vt.BaseFragment
import me.panpf.sketch.sample.vt.BindContentView
import me.panpf.sketch.sample.vt.R
import me.panpf.sketch.sample.vt.bean.BoundaryStatus
import me.panpf.sketch.sample.vt.bean.VideoInfo
import me.panpf.sketch.sample.vt.item.LoadMoreItemFactory
import me.panpf.sketch.sample.vt.item.VideoInfoItemFactory
import me.panpf.sketch.sample.vt.vm.VideoListViewModel
import java.io.File

@BindContentView(R.layout.fragment_recycler)
class VideoListFragment : BaseFragment() {

    private val videoListViewModel: VideoListViewModel by bindViewModel(VideoListViewModel::class)

    private val adapter = AssemblyPagedListAdapter<VideoInfo>(VideoInfo.DiffCallback()).apply {
        addItemFactory(VideoInfoItemFactory().setOnItemClickListener { _, _, _, _, data ->
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(Uri.fromFile(File(data?.path)), data?.mimeType)
            }

            try {
                startActivity(intent)
            } catch (e: Throwable) {
                e.printStackTrace()
                showLongToast("Not found can play video app")
            }
        })
        setMoreItem(LoadMoreItemFactory())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerFragment_contentRecycler.apply {
            setPadding(checkNotNull(context).dp2px(2))
            clipToPadding = false

            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
            this@apply.adapter = this@VideoListFragment.adapter
        }

//        recyclerFragment_refreshLayout.apply { setOnRefreshListener { videoListViewModel.refresh() } }

        recyclerFragment_refreshLayout.apply { setOnRefreshListener {
            videoListViewModel.getVideoListing(true).observe(this@VideoListFragment, Observer {
                adapter.submitList(it)
            })
        } }

        videoListViewModel.getVideoListing().observe(this, Observer { adapter.submitList(it) })

        videoListViewModel.initStatus.observe(this, Observer { initStatus ->
            initStatus ?: return@Observer

            if (recyclerFragment_refreshLayout.isRefreshing) {
                when {
                    initStatus.isLoading() -> {
                        recyclerFragment_loadingText.visibility = View.GONE
                    }
                    initStatus.isError() -> {
                        recyclerFragment_refreshLayout.isRefreshing = false

                        recyclerFragment_loadingText.text = getString(R.string.hint_loadFailed, initStatus.message)
                        recyclerFragment_loadingText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_error, 0, 0)
                        recyclerFragment_loadingText.visibility = View.VISIBLE
                    }
                    initStatus.isSuccess() -> {
                        recyclerFragment_refreshLayout.isRefreshing = false

                        if (videoListViewModel.boundaryStatus.value == BoundaryStatus.ZERO_ITEMS_LOADED) {
                            recyclerFragment_loadingText.text = getString(R.string.hint_empty_list, "Video")
                            recyclerFragment_loadingText.visibility = View.VISIBLE
                        } else {
                            recyclerFragment_loadingText.visibility = View.GONE
                        }
                    }
                }
            } else {
                when {
                    initStatus.isLoading() -> {
                        recyclerFragment_loadingText.setText(R.string.hint_loading)
                        recyclerFragment_loadingText.setCompoundDrawables(null, null, null, null)
                        recyclerFragment_loadingText.visibility = View.VISIBLE
                    }
                    initStatus.isError() -> {
                        recyclerFragment_loadingText.text = getString(R.string.hint_loadFailed, initStatus.message)
                        recyclerFragment_loadingText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_error, 0, 0)
                        recyclerFragment_loadingText.visibility = View.VISIBLE
                    }
                    initStatus.isSuccess() -> {
                        if (videoListViewModel.boundaryStatus.value == BoundaryStatus.ZERO_ITEMS_LOADED) {
                            recyclerFragment_loadingText.text = getString(R.string.hint_empty_list, "Video")
                            recyclerFragment_loadingText.visibility = View.VISIBLE
                        } else {
                            recyclerFragment_loadingText.visibility = View.GONE
                        }
                    }
                }
            }
        })

        videoListViewModel.pagingStatus.observe(this, Observer { pagingStatus ->
            pagingStatus ?: return@Observer

            when {
                pagingStatus.isLoading() -> {
                    adapter.loadMoreFinished(false)
                }
                pagingStatus.isError() -> {
                    adapter.loadMoreFailed()
                }
            }
        })

        videoListViewModel.boundaryStatus.observe(this, Observer {
            if (it == BoundaryStatus.ITEM_AT_END_LOADED) {
                adapter.loadMoreFinished(true)
            }
        })
    }
}
