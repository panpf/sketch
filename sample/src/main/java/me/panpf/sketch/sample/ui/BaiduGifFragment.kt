package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.flexbox.FlexboxLayoutManager
import me.panpf.adapter.AssemblyAdapter
import me.panpf.adapter.AssemblyRecyclerAdapter
import me.panpf.adapter.more.OnLoadMoreListener
import me.panpf.sketch.SketchImageView
import me.panpf.sketch.sample.base.BaseBindingFragment
import me.panpf.sketch.sample.bean.BaiduImage
import me.panpf.sketch.sample.bean.BaiduImageSearchResult
import me.panpf.sketch.sample.bean.Image
import me.panpf.sketch.sample.databinding.FragmentRecyclerBinding
import me.panpf.sketch.sample.item.BaiduStaggeredImageItem
import me.panpf.sketch.sample.item.LoadMoreItem
import me.panpf.sketch.sample.net.NetServices
import me.panpf.sketch.sample.util.ScrollingPauseLoadManager
import me.panpf.sketch.sample.widget.HintView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference

class BaiduGifFragment : BaseBindingFragment<FragmentRecyclerBinding>(),
    SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener {

    private var pageIndex = 1
    private var adapter: AssemblyRecyclerAdapter? = null

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentRecyclerBinding.inflate(inflater, parent, false)

    override fun onInitData(binding: FragmentRecyclerBinding, savedInstanceState: Bundle?) {
        binding.refreshRecyclerFragment.setOnRefreshListener(this)

        binding.recyclerRecyclerFragmentContent.apply {
            addOnScrollListener(ScrollingPauseLoadManager(requireContext()))
            layoutManager = FlexboxLayoutManager(context)
            addItemDecoration(FlexboxItemDecoration(context))
        }

        if (adapter == null) {
            binding.refreshRecyclerFragment.post { onRefresh() }
        } else {
            setAdapter(binding, adapter)
        }
    }

    private fun setAdapter(binding: FragmentRecyclerBinding, adapter: AssemblyRecyclerAdapter?) {
        binding.recyclerRecyclerFragmentContent.adapter = adapter
        binding.recyclerRecyclerFragmentContent.scheduleLayoutAnimation()
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
        NetServices.baiduImage().searchPhoto("GIF", "GIF", pageStart, PAGE_SIZE)
            .enqueue(LoadDataCallback(this, pageIndex))
    }

    override fun onLoadMore(adapter1: AssemblyAdapter) {
        loadData(pageIndex + 1)
    }

    private class LoadDataCallback(fragment: BaiduGifFragment, private val pageIndex: Int) :
        Callback<BaiduImageSearchResult> {

        private val reference: WeakReference<BaiduGifFragment> = WeakReference(fragment)

        init {
            if (pageIndex == 1) {
                fragment.binding?.hintRecyclerFragment?.hidden()
            }
        }

        private fun filterEmptyImage(response: Response<BaiduImageSearchResult>) {
            val imageList = response.body()!!.imageList ?: return

            val mutableImageList = imageList.toMutableList()
            val imageIterator = mutableImageList.iterator()
            while (imageIterator.hasNext()) {
                val image = imageIterator.next()
                if (image.url == null || "" == image.url) {
                    imageIterator.remove()
                }
            }
            response.body()!!.imageList = mutableImageList
        }

        override fun onResponse(
            call: Call<BaiduImageSearchResult>,
            response: Response<BaiduImageSearchResult>
        ) {
            val fragment = reference.get() ?: return
            if (!fragment.isViewCreated) {
                return
            }

            filterEmptyImage(response)

            if (pageIndex == 1) {
                create(fragment, response)
            } else {
                loadMore(fragment, response)
            }

            fragment.binding?.refreshRecyclerFragment?.isRefreshing = false
        }

        override fun onFailure(call: Call<BaiduImageSearchResult>, t: Throwable) {
            val fragment = reference.get() ?: return
            val activity = fragment.activity ?: return
            if (!fragment.isViewCreated) {
                return
            }

            if (pageIndex == 1) {
                fragment.binding?.hintRecyclerFragment?.failed(t) {
                    fragment.onRefresh()
                }
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

        private fun create(fragment: BaiduGifFragment, response: Response<BaiduImageSearchResult>) {

            val images = response.body()?.imageList
            if (images == null || images.isEmpty()) {
                fragment.binding?.hintRecyclerFragment?.empty("No photos")
                return
            }

            val adapter = AssemblyRecyclerAdapter(images)
            adapter.addItemFactory(
                BaiduStaggeredImageItem.Factory()
                    .setOnItemClickListener { _, view, position, _, _ ->
                        val activity = fragment.activity ?: return@setOnItemClickListener

                        @Suppress("UNCHECKED_CAST")
                        val imageList = adapter.dataList as List<BaiduImage>
                        val urlList = imageList.map { Image(it.url.orEmpty(), it.url.orEmpty()) }
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

            fragment.binding?.recyclerRecyclerFragmentContent?.adapter = adapter
            fragment.adapter = adapter
        }

        private fun loadMore(
            fragment: BaiduGifFragment,
            response: Response<BaiduImageSearchResult>
        ) {
            val images = response.body()?.imageList
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