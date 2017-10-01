package me.xiaopan.sketchsample.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import me.xiaopan.assemblyadapter.AssemblyRecyclerAdapter
import me.xiaopan.assemblyadapter.OnRecyclerLoadMoreListener
import me.xiaopan.sketchsample.BaseFragment
import me.xiaopan.sketchsample.BindContentView
import me.xiaopan.sketchsample.R
import me.xiaopan.sketchsample.activity.ImageDetailActivity
import me.xiaopan.sketchsample.activity.PageBackgApplyCallback
import me.xiaopan.sketchsample.adapter.itemfactory.LoadMoreItemFactory
import me.xiaopan.sketchsample.adapter.itemfactory.UnsplashPhotosItemFactory
import me.xiaopan.sketchsample.bean.Image
import me.xiaopan.sketchsample.bean.UnsplashImage
import me.xiaopan.sketchsample.net.NetServices
import me.xiaopan.sketchsample.widget.HintView
import me.xiaopan.ssvt.bindView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference
import java.util.*

@BindContentView(R.layout.fragment_recycler)
class UnsplashPhotosFragment : BaseFragment(), UnsplashPhotosItemFactory.UnsplashPhotosItemEventListener, OnRecyclerLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    val hintView: HintView by bindView(R.id.hint_recyclerFragment)
    val recyclerView: RecyclerView by bindView(R.id.recycler_recyclerFragment_content)
    val refreshLayout: SwipeRefreshLayout by bindView(R.id.refresh_recyclerFragment)

    private var adapter: AssemblyRecyclerAdapter? = null
    private var pageIndex = 1

    private var pageBackgApplyCallback: PageBackgApplyCallback? = null
    private var backgroundImageUri: String? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (activity is PageBackgApplyCallback) {
            pageBackgApplyCallback = activity as PageBackgApplyCallback
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)

        refreshLayout.setOnRefreshListener(this)

        if (adapter != null) {
            recyclerView.adapter = adapter
        } else {
            refreshLayout.post { onRefresh() }
        }
    }


    override fun onUserVisibleChanged(isVisibleToUser: Boolean) {
        if (pageBackgApplyCallback != null && isVisibleToUser) {
            changeBackground(backgroundImageUri)
        }
    }

    private fun changeBackground(imageUri: String?) {
        this.backgroundImageUri = imageUri
        pageBackgApplyCallback?.onApplyBackground(backgroundImageUri)
    }

    private fun loadData(pageIndex: Int) {
        this.pageIndex = pageIndex
        NetServices.unsplash().listPhotos(pageIndex).enqueue(LoadDataCallback(this, pageIndex))
    }

    override fun onClickImage(position: Int, image: UnsplashImage, optionsKey: String?) {
        var optionsKey = optionsKey
        // 含有这些信息时，说明这张图片不仅仅是缩小，而是会被改变，因此不能用作loading图了
        if (optionsKey!!.contains("Resize")
                || optionsKey.contains("ImageProcessor")
                || optionsKey.contains("thumbnailMode")) {
            optionsKey = null
        }

        val images = adapter!!.dataList as List<UnsplashImage>
        val imageArrayList = ArrayList<Image>(images.size)
        for (unsplashImage in images) {
            imageArrayList.add(Image(unsplashImage.urls.regular, unsplashImage.urls.raw))
        }

        ImageDetailActivity.launch(activity, dataTransferHelper.put("urlList", imageArrayList), optionsKey!!, position)
    }

    override fun onClickUser(position: Int, user: UnsplashImage.User) {
        val uri = Uri.parse(user.links.html)
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
            adapter!!.setLoadMoreEnd(false)
        }

        if (!refreshLayout.isRefreshing) {
            refreshLayout.isRefreshing = true
        }

        loadData(1)
    }

    override fun onLoadMore(assemblyRecyclerAdapter: AssemblyRecyclerAdapter) {
        loadData(pageIndex + 1)
    }

    private class LoadDataCallback internal constructor(fragment: UnsplashPhotosFragment, private val pageIndex: Int) : Callback<List<UnsplashImage>> {
        private val reference: WeakReference<UnsplashPhotosFragment>

        init {
            this.reference = WeakReference(fragment)

            if (pageIndex == 1) {
                fragment.hintView.hidden()
            }
        }

        override fun onResponse(call: Call<List<UnsplashImage>>, response: Response<List<UnsplashImage>>) {
            val fragment = reference.get() ?: return

            if (pageIndex == 1) {
                create(fragment, response)
            } else {
                loadMore(fragment, response)
            }

            fragment.refreshLayout.isRefreshing = false
        }

        override fun onFailure(call: Call<List<UnsplashImage>>, t: Throwable) {
            val fragment = reference.get() ?: return

            if (pageIndex == 1) {
                fragment.hintView.failed(t) { fragment.onRefresh() }
                fragment.refreshLayout.isRefreshing = false
            } else {
                fragment.adapter!!.loadMoreFailed()
                Toast.makeText(fragment.activity, HintView.getCauseByException(fragment.activity, t), Toast.LENGTH_LONG).show()
            }
        }

        private fun create(fragment: UnsplashPhotosFragment, response: Response<List<UnsplashImage>>) {
            val images = response.body()
            if (images == null || images.isEmpty()) {
                fragment.hintView.empty("No photos")
                return
            }

            val adapter = AssemblyRecyclerAdapter(images)
            adapter.addItemFactory(UnsplashPhotosItemFactory(fragment))
            adapter.setLoadMoreItem(LoadMoreItemFactory(fragment))

            fragment.recyclerView.adapter = adapter
            fragment.adapter = adapter

            fragment.changeBackground(images[0].urls.thumb)
        }

        private fun loadMore(fragment: UnsplashPhotosFragment, response: Response<List<UnsplashImage>>) {
            val images = response.body()
            if (images == null || images.isEmpty()) {
                fragment.adapter!!.setLoadMoreEnd(true)
                return
            }

            fragment.adapter!!.addAll(images)
            fragment.adapter!!.loadMoreFinished(images.size < 20)

            fragment.changeBackground(images[0].urls.thumb)
        }
    }
}
