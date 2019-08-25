package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.fragment_recycler.*
import me.panpf.adapter.AssemblyAdapter
import me.panpf.adapter.AssemblyRecyclerAdapter
import me.panpf.adapter.more.OnLoadMoreListener
import me.panpf.sketch.SketchImageView
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.base.BindContentView
import me.panpf.sketch.sample.bean.Image
import me.panpf.sketch.sample.bean.TenorData
import me.panpf.sketch.sample.bean.TenorSearchResponse
import me.panpf.sketch.sample.item.LoadMoreItem
import me.panpf.sketch.sample.item.StaggeredImageItem
import me.panpf.sketch.sample.net.NetServices
import me.panpf.sketch.sample.util.ScrollingPauseLoadManager
import me.panpf.sketch.sample.widget.HintView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference

@BindContentView(R.layout.fragment_recycler)
class TenorGifFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener {

    private var pageIndex = 1
    private var adapter: AssemblyRecyclerAdapter? = null

    override fun onViewCreated(@NonNull view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refresh_recyclerFragment.setOnRefreshListener(this)

        recycler_recyclerFragment_content.addOnScrollListener(ScrollingPauseLoadManager(view.context))

        recycler_recyclerFragment_content.layoutManager = FlexboxLayoutManager(context)
        recycler_recyclerFragment_content.addItemDecoration(FlexboxItemDecoration(context))

        if (adapter == null) {
            refresh_recyclerFragment.post { onRefresh() }
        } else {
            setAdapter(adapter)
        }
    }

    private fun setAdapter(adapter: AssemblyRecyclerAdapter?) {
        recycler_recyclerFragment_content.adapter = adapter
        recycler_recyclerFragment_content.scheduleLayoutAnimation()
        this.adapter = adapter
    }

    override fun onRefresh() {
        adapter?.loadMoreFinished(false)

        if (!refresh_recyclerFragment.isRefreshing) {
            refresh_recyclerFragment.isRefreshing = true
        }

        loadData(1)
    }

    private fun loadData(pageIndex: Int) {
        this.pageIndex = pageIndex
        val pageStart = (pageIndex - 1) * PAGE_SIZE
        NetServices.tenor().search("young girl", pageStart, PAGE_SIZE).enqueue(LoadDataCallback(this, pageIndex))
    }

    override fun onLoadMore(adapter1: AssemblyAdapter) {
        loadData(pageIndex + 1)
    }

    private class LoadDataCallback internal constructor(fragment: TenorGifFragment, private val pageIndex: Int) : Callback<TenorSearchResponse> {

        private val reference: WeakReference<TenorGifFragment> = WeakReference(fragment)

        init {
            if (pageIndex == 1) {
                fragment.hint_recyclerFragment.hidden()
            }
        }

        override fun onResponse(call: Call<TenorSearchResponse>, response: Response<TenorSearchResponse>) {
            val fragment = reference.get() ?: return
            if (!fragment.isViewCreated) {
                return
            }

            if (pageIndex == 1) {
                create(fragment, response)
            } else {
                loadMore(fragment, response)
            }

            fragment.refresh_recyclerFragment.isRefreshing = false
        }

        override fun onFailure(call: Call<TenorSearchResponse>, t: Throwable) {
            val fragment = reference.get() ?: return
            val activity = fragment.activity ?: return
            if (!fragment.isViewCreated) {
                return
            }

            if (pageIndex == 1) {
                fragment.hint_recyclerFragment.failed(t, View.OnClickListener {
                    fragment.onRefresh()
                })
                fragment.refresh_recyclerFragment.isRefreshing = false
            } else {
                fragment.adapter!!.loadMoreFailed()
                Toast.makeText(fragment.activity, HintView.getCauseByException(activity, t), Toast.LENGTH_LONG).show()
            }
        }

        private fun create(fragment: TenorGifFragment, response: Response<TenorSearchResponse>) {

            val images = response.body()?.dataList
            if (images == null || images.isEmpty()) {
                fragment.hint_recyclerFragment.empty("No photos")
                return
            }

            val adapter = AssemblyRecyclerAdapter(images)
            adapter.addItemFactory(StaggeredImageItem.Factory().setOnItemClickListener { _, view, position, _, _ ->
                val activity = fragment.activity ?: return@setOnItemClickListener
                @Suppress("UNCHECKED_CAST")
                val imageList = adapter.dataList as List<TenorData>
                val urlList = imageList.map { Image(it.gifMedia?.url.orEmpty(), it.gifMedia?.url.orEmpty()) }
                val loadingImageOptionsInfo = (view as SketchImageView).optionsKey
                ImageDetailActivity.launch(activity, fragment.dataTransferHelper.put("urlList", urlList), loadingImageOptionsInfo, position - adapter.headerItemCount)
            })
            adapter.setMoreItem(LoadMoreItem.Factory(fragment).fullSpan(fragment.recycler_recyclerFragment_content))

            fragment.recycler_recyclerFragment_content.adapter = adapter
            fragment.adapter = adapter
        }

        private fun loadMore(fragment: TenorGifFragment, response: Response<TenorSearchResponse>) {
            val images = response.body()?.dataList
            if (images == null || images.isEmpty()) {
                fragment.adapter!!.loadMoreFinished(true)
                return
            }

            fragment.adapter!!.addAll(images)
            fragment.adapter!!.loadMoreFinished(images.size < 20)
        }
    }

    companion object {
        private const val PAGE_SIZE = 40
    }
}
