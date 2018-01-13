package me.panpf.sketch.sample.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.flexbox.FlexboxLayoutManager
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.activity.ImageDetailActivity
import me.panpf.sketch.sample.activity.PageBackgApplyCallback
import me.panpf.sketch.sample.adapter.itemfactory.LoadMoreItemFactory
import me.panpf.sketch.sample.adapter.itemfactory.StaggeredImageItemFactory
import me.panpf.sketch.sample.bean.BaiduImage
import me.panpf.sketch.sample.bean.BaiduImageSearchResult
import me.panpf.sketch.sample.bean.Image
import me.panpf.sketch.sample.bindView
import me.panpf.sketch.sample.net.NetServices
import me.panpf.sketch.sample.util.ScrollingPauseLoadManager
import me.panpf.sketch.sample.widget.HintView
import me.xiaopan.assemblyadapter.AssemblyRecyclerAdapter
import me.xiaopan.assemblyadapter.OnRecyclerLoadMoreListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference

/**
 * 图片搜索Fragment
 */
@BindContentView(R.layout.fragment_recycler)
class SearchFragment : BaseFragment(), StaggeredImageItemFactory.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, OnRecyclerLoadMoreListener {

    val refreshLayout: SwipeRefreshLayout by bindView(R.id.refresh_recyclerFragment)
    val recyclerView: RecyclerView by bindView(R.id.recycler_recyclerFragment_content)
    val hintView: HintView by bindView(R.id.hint_recyclerFragment)

    private var searchKeyword: String? = "GIF"

    private var pageIndex = 1
    private var adapter: AssemblyRecyclerAdapter? = null

    private var pageBackgApplyCallback: PageBackgApplyCallback? = null
    private var backgroundImageUri: String? = null

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        if (activity is PageBackgApplyCallback) {
            pageBackgApplyCallback = activity
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val arguments = arguments
        if (arguments != null) {
            searchKeyword = arguments.getString(PARAM_OPTIONAL_STRING_SEARCH_KEYWORD)
            if (searchKeyword == null) {
                searchKeyword = "GIF"
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setTitle(searchKeyword ?: "")
    }

    private fun setTitle(subtitle: String) {
        if (activity != null && activity is AppCompatActivity) {
            val actionBar = (activity as AppCompatActivity).supportActionBar
            if (actionBar != null) {
                actionBar.subtitle = subtitle
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_search_view, menu)
        val searchView = MenuItemCompat.getActionView(menu!!.findItem(R.id.menu_searchView)) as SearchView
        searchView.queryHint = searchKeyword
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                var s = s
                s = s.trim { it <= ' ' }
                if ("" == s) {
                    Toast.makeText(activity, "搜索关键字不能为空", Toast.LENGTH_LONG).show()
                    return false
                }

                setTitle(s)
                val bundle = Bundle()
                bundle.putString(SearchFragment.PARAM_OPTIONAL_STRING_SEARCH_KEYWORD, s)
                val searchFragment = SearchFragment()
                searchFragment.arguments = bundle
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                        .replace(R.id.frame_main_content, searchFragment)
                        .commit()

                (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .hideSoftInputFromWindow(activity.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

                return true
            }

            override fun onQueryTextChange(s: String): Boolean {
                return false
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshLayout.setOnRefreshListener(this)

        recyclerView.addOnScrollListener(ScrollingPauseLoadManager(view!!.context))

        recyclerView.layoutManager = FlexboxLayoutManager(context)
        recyclerView.addItemDecoration(FlexboxItemDecoration(context))

        if (adapter == null) {
            refreshLayout.post { onRefresh() }
        } else {
            setAdapter(adapter)
        }
    }

    override fun onDestroyView() {
        setTitle("")
        super.onDestroyView()
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

    private fun setAdapter(adapter: AssemblyRecyclerAdapter?) {
        recyclerView.adapter = adapter
        recyclerView.scheduleLayoutAnimation()
        this.adapter = adapter
    }

    override fun onRefresh() {
        adapter?.setLoadMoreEnd(false)

        if (!refreshLayout.isRefreshing) {
            refreshLayout.isRefreshing = true
        }

        loadData(1)
    }

    override fun onItemClick(position: Int, image: BaiduImage, loadingImageOptionsInfo: String) {
        val imageList = adapter!!.dataList as List<BaiduImage>
        val urlList = imageList.map { Image(it.url!!, it.url!!) }
        ImageDetailActivity.launch(activity, dataTransferHelper.put("urlList", urlList), loadingImageOptionsInfo, position - adapter!!.headerItemCount)
    }

    private fun loadData(pageIndex: Int) {
        this.pageIndex = pageIndex
        val pageStart = (pageIndex - 1) * PAGE_SIZE
        NetServices.baiduImage().searchPhoto(searchKeyword, searchKeyword, pageStart, PAGE_SIZE).enqueue(LoadDataCallback(this, pageIndex))
    }

    override fun onLoadMore(assemblyRecyclerAdapter: AssemblyRecyclerAdapter) {
        loadData(pageIndex + 1)
    }

    private class LoadDataCallback internal constructor(fragment: SearchFragment, private val pageIndex: Int) : Callback<BaiduImageSearchResult> {

        private val reference: WeakReference<SearchFragment> = WeakReference(fragment)

        init {
            if (pageIndex == 1) {
                fragment.hintView.hidden()
            }
        }

        override fun onResponse(call: Call<BaiduImageSearchResult>, response: Response<BaiduImageSearchResult>) {
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

            fragment.refreshLayout.isRefreshing = false
        }

        override fun onFailure(call: Call<BaiduImageSearchResult>, t: Throwable) {
            val fragment = reference.get() ?: return
            if (!fragment.isViewCreated) {
                return
            }

            if (pageIndex == 1) {
                fragment.hintView.failed(t, View.OnClickListener {
                    fragment.onRefresh()
                })
                fragment.refreshLayout.isRefreshing = false
            } else {
                fragment.adapter!!.loadMoreFailed()
                Toast.makeText(fragment.activity, HintView.getCauseByException(fragment.activity, t), Toast.LENGTH_LONG).show()
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

        private fun create(fragment: SearchFragment, response: Response<BaiduImageSearchResult>) {

            val images = response.body()!!.imageList
            if (images == null || images.size == 0) {
                fragment.hintView.empty("No photos")
                return
            }

            val adapter = AssemblyRecyclerAdapter(images)
            adapter.addItemFactory(StaggeredImageItemFactory(fragment))
            adapter.setLoadMoreItem(LoadMoreItemFactory(fragment).fullSpan(fragment.recyclerView))

            fragment.recyclerView.adapter = adapter
            fragment.adapter = adapter

            fragment.changeBackground(images[0].url)
        }

        private fun loadMore(fragment: SearchFragment, response: Response<BaiduImageSearchResult>) {

            val images = response.body()!!.imageList
            if (images == null || images.size == 0) {
                fragment.adapter!!.setLoadMoreEnd(true)
                return
            }

            fragment.adapter!!.addAll(images)
            fragment.adapter!!.loadMoreFinished(images.size < 20)

            fragment.changeBackground(images[0].url)
        }
    }

    companion object {
        val PARAM_OPTIONAL_STRING_SEARCH_KEYWORD = "PARAM_OPTIONAL_STRING_SEARCH_KEYWORD"
        private val PAGE_SIZE = 60
    }
}
