package me.panpf.sketch.sample.ui

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_recycler.*
import me.panpf.adapter.AssemblyAdapter
import me.panpf.adapter.AssemblyRecyclerAdapter
import me.panpf.adapter.more.OnLoadMoreListener
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.base.BindContentView
import me.panpf.sketch.sample.bean.Image
import me.panpf.sketch.sample.bean.UnsplashImage
import me.panpf.sketch.sample.item.LoadMoreItem
import me.panpf.sketch.sample.item.UnsplashPhotosItemFactory
import me.panpf.sketch.sample.net.NetServices
import me.panpf.sketch.sample.util.ScrollingPauseLoadManager
import me.panpf.sketch.sample.widget.HintView
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference
import java.util.*

@BindContentView(R.layout.fragment_recycler)
class UnsplashFragment : BaseFragment(), UnsplashPhotosItemFactory.UnsplashPhotosItemEventListener, OnLoadMoreListener, androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener {

    private var adapter: AssemblyRecyclerAdapter? = null
    private var pageIndex = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_recyclerFragment_content.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        recycler_recyclerFragment_content.addOnScrollListener(ScrollingPauseLoadManager(view.context))

        refresh_recyclerFragment.setOnRefreshListener(this)

        if (adapter != null) {
            recycler_recyclerFragment_content.adapter = adapter
        } else {
            refresh_recyclerFragment.post { onRefresh() }
        }
    }

    private fun loadData(pageIndex: Int) {
        this.pageIndex = pageIndex
        NetServices.unsplash().listPhotos(pageIndex).enqueue(LoadDataCallback(this, pageIndex))
    }

    override fun onClickImage(position: Int, image: UnsplashImage, optionsKey: String) {
        val activity = activity ?: return
        var finalOptionsKey: String? = optionsKey
        // 含有这些信息时，说明这张图片不仅仅是缩小，而是会被改变，因此不能用作loading图了
        if (finalOptionsKey!!.contains("Resize")
                || finalOptionsKey.contains("ImageProcessor")
                || finalOptionsKey.contains("thumbnailMode")) {
            finalOptionsKey = null
        }

        @Suppress("UNCHECKED_CAST")
        val images = adapter!!.dataList as List<UnsplashImage>
        val imageArrayList = ArrayList<Image>(images.size)
        images.mapTo(imageArrayList) { Image(it.urls!!.regular!!, it.urls!!.raw!!) }

        ImageDetailActivity.launch(activity, dataTransferHelper.put("urlList", imageArrayList), finalOptionsKey!!, position)
    }

    override fun onClickUser(position: Int, user: UnsplashImage.User) {
        val uri = Uri.parse(user.links!!.html)
                .buildUpon()
                .appendQueryParameter("utm_source", "SketchSample")
                .appendQueryParameter("utm_medium", "referral")
                .appendQueryParameter("utm_campaign", "api-credit")
                .build()
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = uri
        startActivity(intent)
    }

    override fun onRefresh() {
        if (adapter != null) {
            adapter!!.loadMoreFinished(false)
        }

        if (!refresh_recyclerFragment.isRefreshing) {
            refresh_recyclerFragment.isRefreshing = true
        }

        loadData(1)
    }

    override fun onLoadMore(adapter1: AssemblyAdapter) {
        loadData(pageIndex + 1)
    }

    private class LoadDataCallback internal constructor(fragment: UnsplashFragment, private val pageIndex: Int) : Callback<List<UnsplashImage>> {
        private val reference: WeakReference<UnsplashFragment> = WeakReference(fragment)

        init {
            if (pageIndex == 1) {
                fragment.hint_recyclerFragment.hidden()
            }
        }

        override fun onResponse(call: Call<List<UnsplashImage>>, response: Response<List<UnsplashImage>>) {
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

        override fun onFailure(call: Call<List<UnsplashImage>>, t: Throwable) {
            val fragment = reference.get() ?: return
            val activity = fragment.activity ?: return
            if (!fragment.isViewCreated) {
                return
            }

            if (pageIndex == 1) {
                fragment.hint_recyclerFragment.failed(t, View.OnClickListener { fragment.onRefresh() })
                fragment.refresh_recyclerFragment.isRefreshing = false
            } else {
                fragment.adapter!!.loadMoreFailed()
                Toast.makeText(fragment.activity, HintView.getCauseByException(activity, t), Toast.LENGTH_LONG).show()
            }
        }

        private fun create(fragment: UnsplashFragment, response: Response<List<UnsplashImage>>) {
            val activity = fragment.activity ?: return
            val images = response.body()
            if (images == null || images.isEmpty()) {
                fragment.hint_recyclerFragment.empty("No photos")
                return
            }

            val adapter = AssemblyRecyclerAdapter(images)
            adapter.addItemFactory(UnsplashPhotosItemFactory(activity, fragment))
            adapter.setMoreItem(LoadMoreItem.Factory(fragment))

            fragment.recycler_recyclerFragment_content.adapter = adapter
            fragment.adapter = adapter
        }

        private fun loadMore(fragment: UnsplashFragment, response: Response<List<UnsplashImage>>) {
            val images = response.body()
            if (images == null || images.isEmpty()) {
                fragment.adapter!!.loadMoreFinished(true)
                return
            }

            fragment.adapter!!.addAll(images)
            fragment.adapter!!.loadMoreFinished(images.size < 20)
        }
    }
}
