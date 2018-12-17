package me.panpf.sketch.sample.ui

import android.content.Context
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.core.view.MenuItemCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.fragment_recycler.*
import me.panpf.adapter.AssemblyAdapter
import me.panpf.adapter.AssemblyRecyclerAdapter
import me.panpf.adapter.more.OnLoadMoreListener
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.base.BindContentView
import me.panpf.sketch.sample.bean.BaiduImage
import me.panpf.sketch.sample.bean.BaiduImageSearchResult
import me.panpf.sketch.sample.bean.Image
import me.panpf.sketch.sample.event.ChangeMainPageBgEvent
import me.panpf.sketch.sample.item.LoadMoreItemFactory
import me.panpf.sketch.sample.item.StaggeredImageItemFactory
import me.panpf.sketch.sample.net.NetServices
import me.panpf.sketch.sample.util.ScrollingPauseLoadManager
import me.panpf.sketch.sample.widget.HintView
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference

/**
 * 图片搜索Fragment
 */
@BindContentView(R.layout.fragment_recycler)
class SearchFragment : BaseFragment(), StaggeredImageItemFactory.OnItemClickListener, androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener {

    private var searchKeyword: String? = "GIF"

    private var pageIndex = 1
    private var adapter: AssemblyRecyclerAdapter? = null

    private var backgroundImageUri: String? = null

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.menu_search_view, menu)
        @Suppress("DEPRECATION")
        val searchView = MenuItemCompat.getActionView(menu!!.findItem(R.id.menu_searchView)) as SearchView
        searchView.queryHint = searchKeyword
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                val activity = activity ?: return false
                val fragmentManager = fragmentManager ?: return false

                val keyword = s.trim { it <= ' ' }
                if ("" == keyword) {
                    Toast.makeText(activity, "搜索关键字不能为空", Toast.LENGTH_LONG).show()
                    return false
                }

                setTitle(keyword)
                val bundle = Bundle()
                bundle.putString(PARAM_OPTIONAL_STRING_SEARCH_KEYWORD, keyword)
                val searchFragment = SearchFragment()
                searchFragment.arguments = bundle
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                        .replace(R.id.mainFm_contentFrame, searchFragment)
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

    override fun onDestroyView() {
        setTitle("")
        super.onDestroyView()
    }

    override fun onUserVisibleChanged(isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            changeBackground(backgroundImageUri)
        }
    }

    private fun changeBackground(imageUri: String?) {
        this.backgroundImageUri = imageUri
        backgroundImageUri?.let { EventBus.getDefault().post(ChangeMainPageBgEvent(it)) }
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

    override fun onItemClick(position: Int, image: BaiduImage, loadingImageOptionsInfo: String) {
        val activity = activity ?: return
        @Suppress("UNCHECKED_CAST")
        val imageList = adapter!!.dataList as List<BaiduImage>
        val urlList = imageList.map { Image(it.url!!, it.url!!) }
        ImageDetailActivity.launch(activity, dataTransferHelper.put("urlList", urlList), loadingImageOptionsInfo, position - adapter!!.headerItemCount)
    }

    private fun loadData(pageIndex: Int) {
        this.pageIndex = pageIndex
        val pageStart = (pageIndex - 1) * PAGE_SIZE
        NetServices.baiduImage().searchPhoto(searchKeyword, searchKeyword, pageStart, PAGE_SIZE).enqueue(LoadDataCallback(this, pageIndex))
    }

    override fun onLoadMore(adapter1: AssemblyAdapter) {
        loadData(pageIndex + 1)
    }

    private class LoadDataCallback internal constructor(fragment: SearchFragment, private val pageIndex: Int) : Callback<BaiduImageSearchResult> {

        private val reference: WeakReference<SearchFragment> = WeakReference(fragment)

        init {
            if (pageIndex == 1) {
                fragment.hint_recyclerFragment.hidden()
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

            fragment.refresh_recyclerFragment.isRefreshing = false
        }

        override fun onFailure(call: Call<BaiduImageSearchResult>, t: Throwable) {
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
                fragment.hint_recyclerFragment.empty("No photos")
                return
            }

            val adapter = AssemblyRecyclerAdapter(images)
            adapter.addItemFactory(StaggeredImageItemFactory(fragment))
            adapter.setMoreItem(LoadMoreItemFactory(fragment).fullSpan(fragment.recycler_recyclerFragment_content))

            fragment.recycler_recyclerFragment_content.adapter = adapter
            fragment.adapter = adapter

            fragment.changeBackground(images[0].url)
        }

        private fun loadMore(fragment: SearchFragment, response: Response<BaiduImageSearchResult>) {

            val images = response.body()!!.imageList
            if (images == null || images.size == 0) {
                fragment.adapter!!.loadMoreFinished(true)
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
