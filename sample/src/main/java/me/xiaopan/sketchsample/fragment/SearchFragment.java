package me.xiaopan.sketchsample.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectExtra;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.assemblyadapter.AssemblyRecyclerAdapter;
import me.xiaopan.assemblyadapter.OnRecyclerLoadMoreListener;
import me.xiaopan.gohttp.GoHttp;
import me.xiaopan.gohttp.HttpRequest;
import me.xiaopan.gohttp.HttpRequestFuture;
import me.xiaopan.gohttp.JsonHttpResponseHandler;
import me.xiaopan.prl.PullRefreshLayout;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.activity.ApplyBackgroundCallback;
import me.xiaopan.sketchsample.activity.DetailActivity;
import me.xiaopan.sketchsample.adapter.itemfactory.LoadMoreItemFactory;
import me.xiaopan.sketchsample.adapter.itemfactory.StaggeredImageItemFactory;
import me.xiaopan.sketchsample.net.request.SearchImageRequest;
import me.xiaopan.sketchsample.net.request.StarImageRequest;
import me.xiaopan.sketchsample.util.ScrollingPauseLoadManager;
import me.xiaopan.sketchsample.util.Settings;
import me.xiaopan.sketchsample.widget.HintView;

/**
 * 图片搜索Fragment
 */
@InjectContentView(R.layout.fragment_search)
public class SearchFragment extends MyFragment implements StaggeredImageItemFactory.OnItemClickListener, PullRefreshLayout.OnRefreshListener, OnRecyclerLoadMoreListener {
    public static final String PARAM_OPTIONAL_STRING_SEARCH_KEYWORD = "PARAM_OPTIONAL_STRING_SEARCH_KEYWORD";

    @InjectView(R.id.refreshLayout_search)
    PullRefreshLayout pullRefreshLayout;
    @InjectView(R.id.list_search)
    private RecyclerView recyclerView;
    @InjectView(R.id.hintView_search)
    private HintView hintView;

    @InjectExtra(PARAM_OPTIONAL_STRING_SEARCH_KEYWORD)
    private String searchKeyword = "GIF";

    private SearchImageRequest searchImageRequest;
    private HttpRequestFuture refreshRequest;
    private HttpRequestFuture loadMoreRequest;
    private AssemblyRecyclerAdapter adapter;

    private ApplyBackgroundCallback applyBackgroundCallback;
    private String backgroundImageUri;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ApplyBackgroundCallback) {
            applyBackgroundCallback = (ApplyBackgroundCallback) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchImageRequest = new SearchImageRequest(searchKeyword);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setTitle(searchKeyword);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void setTitle(String subtitle) {
        if (getActivity() != null && getActivity() instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(subtitle);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_view, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_searchView));
        searchView.setQueryHint(searchKeyword);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                s = s.trim();
                if ("".equals(s)) {
                    Toast.makeText(getActivity(), "搜索关键字不能为空", Toast.LENGTH_LONG).show();
                    return false;
                }

                setTitle(s);
                Bundle bundle = new Bundle();
                bundle.putString(SearchFragment.PARAM_OPTIONAL_STRING_SEARCH_KEYWORD, s);
                SearchFragment searchFragment = new SearchFragment();
                searchFragment.setArguments(bundle);
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.window_push_enter, R.anim.window_push_exit)
                        .replace(R.id.frame_main_content, searchFragment)
                        .commit();

                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(getActivity().getCurrentFocus()
                                        .getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pullRefreshLayout.setOnRefreshListener(this);

        recyclerView.setOnScrollListener(new ScrollingPauseLoadManager(view.getContext()));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        int padding  = SketchUtils.dp2px(getActivity(), 4);
        recyclerView.setPadding(padding, padding, padding, padding);
        recyclerView.setClipToPadding(false);

        if (adapter == null) {
            pullRefreshLayout.startRefresh();
        } else {
            setAdapter(adapter);
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        setTitle("");
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        if (refreshRequest != null && !refreshRequest.isFinished()) {
            refreshRequest.cancel(true);
        }
        super.onDetach();
    }

    @Override
    protected void onUserVisibleChanged(boolean isVisibleToUser) {
        if (applyBackgroundCallback != null && isVisibleToUser) {
            changeBackground(backgroundImageUri);
        }
    }

    private void changeBackground(String imageUri) {
        this.backgroundImageUri = imageUri;
        if (applyBackgroundCallback != null) {
            applyBackgroundCallback.onApplyBackground(backgroundImageUri);
        }
    }

    private void setAdapter(AssemblyRecyclerAdapter adapter) {
        recyclerView.setAdapter(adapter);
        recyclerView.scheduleLayoutAnimation();
        this.adapter = adapter;
    }

    @Override
    public void onRefresh() {
        if (refreshRequest != null && !refreshRequest.isFinished()) {
            return;
        }

        if (loadMoreRequest != null && !loadMoreRequest.isFinished()) {
            loadMoreRequest.cancel(true);
        }

        if (adapter != null) {
            adapter.setLoadMoreEnd(false);
        }

        searchImageRequest.setStart(0);
        refreshRequest = GoHttp.with(getActivity()).newRequest(searchImageRequest, new JsonHttpResponseHandler(SearchImageRequest.Response.class), new HttpRequest.Listener<SearchImageRequest.Response>() {
            @Override
            public void onStarted(HttpRequest httpRequest) {
                hintView.hidden();
            }

            @Override
            public void onCompleted(HttpRequest httpRequest, HttpResponse httpResponse, SearchImageRequest.Response responseObject, boolean b, boolean b2) {
                if (getActivity() == null) {
                    return;
                }

                if (responseObject == null || responseObject.getImages() == null || responseObject.getImages().size() == 0) {
                    hintView.failed(new HttpRequest.Failure(0, "咦，图片去哪儿了？"), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pullRefreshLayout.startRefresh();
                        }
                    });
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pullRefreshLayout.stopRefresh();
                        }
                    }, 500);
                } else {
                    AssemblyRecyclerAdapter adapter = new AssemblyRecyclerAdapter(responseObject.getImages());
                    adapter.addItemFactory(new StaggeredImageItemFactory(SearchFragment.this));
                    adapter.setLoadMoreItem(new LoadMoreItemFactory(SearchFragment.this).fullSpan(recyclerView));
                    setAdapter(adapter);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pullRefreshLayout.stopRefresh();
                        }
                    }, 500);

                    if (responseObject.getImages().size() > 0) {
                        changeBackground(responseObject.getImages().get(0).getSourceUrl());
                    }
                }
            }

            @Override
            public void onFailed(HttpRequest httpRequest, HttpResponse httpResponse, HttpRequest.Failure failure, boolean b, boolean b2) {
                if (getActivity() == null) {
                    return;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullRefreshLayout.stopRefresh();
                    }
                }, 500);
                if (adapter == null) {
                    hintView.failed(failure, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pullRefreshLayout.startRefresh();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "刷新失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCanceled(HttpRequest httpRequest) {
            }
        }).responseHandleCompletedAfterListener(new SearchImageRequest.ResponseHandler()).go();
    }

    @Override
    public void onItemClick(int position, StarImageRequest.Image image, String loadingImageOptionsInfo) {
        List<StarImageRequest.Image> imageList = adapter.getDataList();
        ArrayList urlList = new ArrayList<String>();
        for (StarImageRequest.Image imageItem : imageList) {
            urlList.add(imageItem.getSourceUrl());
        }
        DetailActivity.launch(getActivity(), urlList, loadingImageOptionsInfo, position - adapter.getHeaderItemCount());
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onGlobalAttrChanged(String key){
        if (Settings.PREFERENCE_PLAY_GIF_ON_LIST.equals(key)
                || Settings.PREFERENCE_GLOBAL_IN_PREFER_QUALITY_OVER_SPEED.equals(key)
                || Settings.PREFERENCE_GLOBAL_LOW_QUALITY_IMAGE.equals(key)
                || Settings.PREFERENCE_THUMBNAIL_MODE.equals(key)
                || Settings.PREFERENCE_CACHE_PROCESSED_IMAGE.equals(key)
                || Settings.PREFERENCE_SCROLLING_PAUSE_LOAD.equals(key)
                || Settings.PREFERENCE_MOBILE_NETWORK_PAUSE_DOWNLOAD.equals(key)) {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLoadMore(AssemblyRecyclerAdapter assemblyRecyclerAdapter) {
        searchImageRequest.setStart(adapter.getDataCount());
        loadMoreRequest = GoHttp.with(getActivity()).newRequest(searchImageRequest, new JsonHttpResponseHandler(SearchImageRequest.Response.class), new HttpRequest.Listener<SearchImageRequest.Response>() {
            @Override
            public void onStarted(HttpRequest httpRequest) {

            }

            @Override
            public void onCompleted(HttpRequest httpRequest, HttpResponse httpResponse, SearchImageRequest.Response responseObject, boolean b, boolean b2) {
                if (getActivity() == null) {
                    return;
                }

                List<StarImageRequest.Image> newImageList = null;
                if (responseObject.getImages() != null) {
                    newImageList = new ArrayList<StarImageRequest.Image>();
                    for (SearchImageRequest.Image image : responseObject.getImages()) {
                        newImageList.add(image);
                    }
                }

                if (newImageList != null && newImageList.size() > 0) {
                    adapter.addAll(newImageList);
                    if (newImageList.size() < searchImageRequest.getSize()) {
                        adapter.setLoadMoreEnd(true);
                        Toast.makeText(getActivity(), "新送达" + newImageList.size() + "个包裹，已全部送完！", Toast.LENGTH_SHORT).show();
                    } else {
                        adapter.setLoadMoreEnd(false);
                        Toast.makeText(getActivity(), "新送达" + newImageList.size() + "个包裹", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    adapter.setLoadMoreEnd(true);
                    Toast.makeText(getActivity(), "没有您的包裹了", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(HttpRequest httpRequest, HttpResponse httpResponse, HttpRequest.Failure failure, boolean b, boolean b2) {
                if (getActivity() == null) {
                    return;
                }
                adapter.loadMoreFailed();
                Toast.makeText(getActivity(), "快递投递失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCanceled(HttpRequest httpRequest) {
                adapter.loadMoreFailed();
            }
        }).responseHandleCompletedAfterListener(new SearchImageRequest.ResponseHandler()).go();
    }
}
