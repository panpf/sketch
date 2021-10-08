package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.flexbox.FlexboxLayoutManager
import me.panpf.adapter.AssemblyAdapter
import me.panpf.adapter.AssemblyRecyclerAdapter
import me.panpf.adapter.more.OnLoadMoreListener
import me.panpf.sketch.SketchImageView
import me.panpf.sketch.sample.base.BaseToolbarFragment
import me.panpf.sketch.sample.bean.Image
import me.panpf.sketch.sample.bean.TenorData
import me.panpf.sketch.sample.bean.TenorSearchResponse
import me.panpf.sketch.sample.databinding.FragmentRecyclerBinding
import me.panpf.sketch.sample.item.LoadMoreItem
import me.panpf.sketch.sample.item.StaggeredImageItem
import me.panpf.sketch.sample.net.NetServices
import me.panpf.sketch.sample.util.ScrollingPauseLoadManager
import me.panpf.sketch.sample.widget.HintView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference

class TenorGifFragment : BaseToolbarFragment<FragmentRecyclerBinding>(),
    SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener {

    private var pageIndex = 1
    private var adapter: AssemblyRecyclerAdapter? = null

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentRecyclerBinding.inflate(inflater, parent, false)

    override fun onInitData(
        toolbar: Toolbar,
        binding: FragmentRecyclerBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Tenor GIF"

        binding.refreshRecyclerFragment.setOnRefreshListener(this)

        binding.recyclerRecyclerFragmentContent.addOnScrollListener(
            ScrollingPauseLoadManager(
                requireContext()
            )
        )

        binding.recyclerRecyclerFragmentContent.layoutManager = FlexboxLayoutManager(context)
        binding.recyclerRecyclerFragmentContent.addItemDecoration(FlexboxItemDecoration(context))

        if (adapter == null) {
            binding.refreshRecyclerFragment.post { onRefresh() }
        } else {
            setAdapter(adapter)
        }
    }

    private fun setAdapter(adapter: AssemblyRecyclerAdapter?) {
        binding?.recyclerRecyclerFragmentContent?.adapter = adapter
        binding?.recyclerRecyclerFragmentContent?.scheduleLayoutAnimation()
        this.adapter = adapter
    }

    override fun onRefresh() {
        adapter?.loadMoreFinished(false)

        if (binding?.refreshRecyclerFragment?.isRefreshing != true) {
            binding?.refreshRecyclerFragment?.isRefreshing = true
        }

        loadData(1)
    }

    private fun loadData(pageIndex: Int) {
        this.pageIndex = pageIndex
        val pageStart = (pageIndex - 1) * PAGE_SIZE
        NetServices.tenor().search("young girl", pageStart, PAGE_SIZE)
            .enqueue(LoadDataCallback(this, pageIndex))
    }

    override fun onLoadMore(adapter1: AssemblyAdapter) {
        loadData(pageIndex + 1)
    }

    private class LoadDataCallback internal constructor(
        fragment: TenorGifFragment,
        private val pageIndex: Int
    ) : Callback<TenorSearchResponse> {

        private val reference: WeakReference<TenorGifFragment> = WeakReference(fragment)

        init {
            if (pageIndex == 1) {
                fragment.binding?.hintRecyclerFragment?.hidden()
            }
        }

        override fun onResponse(
            call: Call<TenorSearchResponse>,
            response: Response<TenorSearchResponse>
        ) {
            val fragment = reference.get() ?: return
            if (!fragment.isViewCreated) {
                return
            }

            if (pageIndex == 1) {
                create(fragment, response)
            } else {
                loadMore(fragment, response)
            }

            fragment.binding?.refreshRecyclerFragment?.isRefreshing = false
        }

        override fun onFailure(call: Call<TenorSearchResponse>, t: Throwable) {
            val fragment = reference.get() ?: return
            val activity = fragment.activity ?: return
            if (!fragment.isViewCreated) {
                return
            }

            if (pageIndex == 1) {
                fragment.binding?.hintRecyclerFragment?.failed(t, View.OnClickListener {
                    fragment.onRefresh()
                })
                fragment.binding?.refreshRecyclerFragment?.isRefreshing = false
            } else {
                fragment.adapter!!.loadMoreFailed()
                Toast.makeText(
                    fragment.activity,
                    HintView.getCauseByException(activity, t),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        private fun create(fragment: TenorGifFragment, response: Response<TenorSearchResponse>) {

            val images = response.body()?.dataList
            if (images == null || images.isEmpty()) {
                fragment.binding?.hintRecyclerFragment?.empty("No photos")
                return
            }

            val adapter = AssemblyRecyclerAdapter(images)
            adapter.addItemFactory(
                StaggeredImageItem.Factory().setOnItemClickListener { _, view, position, _, _ ->
                    val activity = fragment.activity ?: return@setOnItemClickListener

                    @Suppress("UNCHECKED_CAST")
                    val imageList = adapter.dataList as List<TenorData>
                    val urlList = imageList.map {
                        Image(
                            it.gifMedia?.url.orEmpty(),
                            it.gifMedia?.url.orEmpty()
                        )
                    }
                    val loadingImageOptionsInfo = (view as SketchImageView).optionsKey
                    ImageDetailActivity.launch(
                        activity,
                        fragment.dataTransferHelper.put("urlList", urlList),
                        loadingImageOptionsInfo,
                        position - adapter.headerCount
                    )
                })
            adapter.setMoreItem(
                LoadMoreItem.Factory(fragment)
                    .fullSpan(fragment.binding!!.recyclerRecyclerFragmentContent)
            )

            fragment.binding!!.recyclerRecyclerFragmentContent.adapter = adapter
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
