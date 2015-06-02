package me.xiaopan.sketchsample.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

import me.xiaopan.android.gohttp.GoHttp;
import me.xiaopan.android.gohttp.HttpRequest;
import me.xiaopan.android.gohttp.HttpRequestFuture;
import me.xiaopan.android.gohttp.JsonHttpResponseHandler;
import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectExtra;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.android.widget.PullRefreshLayout;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.activity.DetailActivity;
import me.xiaopan.sketchsample.activity.WindowBackgroundManager;
import me.xiaopan.sketchsample.adapter.ImageStaggeredGridAdapter;
import me.xiaopan.sketchsample.net.request.SearchImageRequest;
import me.xiaopan.sketchsample.net.request.StarImageRequest;
import me.xiaopan.sketchsample.util.ScrollingPauseLoadManager;
import me.xiaopan.sketchsample.widget.HintView;
import me.xiaopan.sketchsample.widget.LoadMoreFooterView;

/**
 * 图片搜索Fragment
 */
@InjectContentView(R.layout.fragment_search)
public class SearchFragment extends MyFragment implements ImageStaggeredGridAdapter.OnItemClickListener, PullRefreshLayout.OnRefreshListener, LoadMoreFooterView.OnLoadMoreListener {
    public static final String PARAM_OPTIONAL_STRING_SEARCH_KEYWORD = "PARAM_OPTIONAL_STRING_SEARCH_KEYWORD";

    @InjectView(R.id.refreshLayout_search) PullRefreshLayout pullRefreshLayout;
    @InjectView(R.id.list_search) private StaggeredGridView staggeredGridView;
    @InjectView(R.id.hintView_search) private HintView hintView;

    private SearchImageRequest searchImageRequest;
    private HttpRequestFuture refreshRequestFuture;
    private HttpRequestFuture loadMoreRequestFuture;
    private ImageStaggeredGridAdapter searchImageListAdapter;
    private WindowBackgroundManager.WindowBackgroundLoader windowBackgroundLoader;
    private LoadMoreFooterView loadMoreFooterView;

    @InjectExtra(PARAM_OPTIONAL_STRING_SEARCH_KEYWORD) private String searchKeyword = "GIF";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity != null && activity instanceof WindowBackgroundManager.OnSetWindowBackgroundListener){
            windowBackgroundLoader = new WindowBackgroundManager.WindowBackgroundLoader(activity.getBaseContext(), (WindowBackgroundManager.OnSetWindowBackgroundListener) activity);
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

    private void setTitle(String subtitle){
        if(getActivity() != null && getActivity() instanceof ActionBarActivity){
            ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
            if(actionBar != null){
                actionBar.setTitle(subtitle);
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

        staggeredGridView.setOnScrollListener(new ScrollingPauseLoadManager(view.getContext()));

        if (searchImageListAdapter == null) {
            pullRefreshLayout.startRefresh();
        } else {
            setAdapter(searchImageListAdapter);
            if(windowBackgroundLoader != null){
                windowBackgroundLoader.restore();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        loadMoreFooterView = null;
    }

    @Override
    public void onDetach() {
        if (refreshRequestFuture != null && !refreshRequestFuture.isFinished()) {
            refreshRequestFuture.cancel(true);
        }
        if(windowBackgroundLoader != null){
            windowBackgroundLoader.detach();
        }
        super.onDetach();
    }

    @Override
    protected void onUserVisibleChanged(boolean isVisibleToUser) {
        if(windowBackgroundLoader != null){
            windowBackgroundLoader.setUserVisible(isVisibleToUser);
        }
    }

    private void setAdapter(ImageStaggeredGridAdapter adapter){
        if(loadMoreFooterView == null){
            loadMoreFooterView = new LoadMoreFooterView(getActivity());
            loadMoreFooterView.setOnLoadMoreListener(this);
            staggeredGridView.setOnGetFooterViewListener(loadMoreFooterView);
            staggeredGridView.addFooterView(loadMoreFooterView);
        }
        staggeredGridView.setAdapter(searchImageListAdapter = adapter);
        staggeredGridView.scheduleLayoutAnimation();
    }

    @Override
    public void onRefresh() {
        if (refreshRequestFuture != null && !refreshRequestFuture.isFinished()) {
            return;
        }

        if(loadMoreRequestFuture != null && !loadMoreRequestFuture.isFinished()){
            loadMoreRequestFuture.cancel(true);
        }

        if(loadMoreFooterView != null){
            loadMoreFooterView.setPause(true);
        }

        searchImageRequest.setStart(0);
        refreshRequestFuture = GoHttp.with(getActivity()).newRequest(searchImageRequest, new JsonHttpResponseHandler(SearchImageRequest.Response.class), new HttpRequest.Listener<SearchImageRequest.Response>() {
            @Override
            public void onStarted(HttpRequest httpRequest) {
                hintView.hidden();
            }

            @Override
            public void onCompleted(HttpRequest httpRequest, HttpResponse httpResponse, SearchImageRequest.Response responseObject, boolean b, boolean b2) {
                if (getActivity() == null) {
                    return;
                }

                List<StarImageRequest.Image> imageList = new ArrayList<StarImageRequest.Image>();
                for (SearchImageRequest.Image image : responseObject.getImages()) {
                    imageList.add(image);
                }
                setAdapter(new ImageStaggeredGridAdapter(getActivity(), staggeredGridView, imageList, SearchFragment.this));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullRefreshLayout.stopRefresh();
                    }
                }, 1000);

                if(loadMoreFooterView != null){
                    loadMoreFooterView.setPause(false);
                    if(loadMoreFooterView.isEnd()){
                        loadMoreFooterView.setEnd(false);
                    }
                }

                if(windowBackgroundLoader != null && imageList.size() > 0){
                    windowBackgroundLoader.load(imageList.get(0).getSourceUrl());
                }
            }

            @Override
            public void onFailed(HttpRequest httpRequest, HttpResponse httpResponse, HttpRequest.Failure failure, boolean b, boolean b2) {
                if (getActivity() == null) {
                    return;
                }
                if(loadMoreFooterView != null){
                    loadMoreFooterView.setPause(false);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullRefreshLayout.stopRefresh();
                    }
                }, 1000);
                if (searchImageListAdapter == null) {
                    hintView.failure(failure, new View.OnClickListener() {
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
                if(loadMoreFooterView != null){
                    loadMoreFooterView.setPause(false);
                }
            }
        }).responseHandleCompletedAfterListener(new SearchImageRequest.ResponseHandler()).go();
    }

    @Override
    public void onItemClick(int position, StarImageRequest.Image image) {
        DetailActivity.launch(getActivity(), (ArrayList<String>) searchImageListAdapter.getImageUrlList(), position);
    }

    @Override
    public void onLoadMore(final LoadMoreFooterView loadMoreFooterView) {
        searchImageRequest.setStart(searchImageListAdapter.getDataSize());
        loadMoreRequestFuture = GoHttp.with(getActivity()).newRequest(searchImageRequest, new JsonHttpResponseHandler(SearchImageRequest.Response.class), new HttpRequest.Listener<SearchImageRequest.Response>() {
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
                    searchImageListAdapter.append(newImageList);
                    if (newImageList.size() < searchImageRequest.getSize()) {
                        loadMoreFooterView.setEnd(true);
                        Toast.makeText(getActivity(), "新送达" + newImageList.size() + "个包裹，已全部送完！", Toast.LENGTH_SHORT).show();
                    } else {
                        loadMoreFooterView.loadFinished(true);
                        Toast.makeText(getActivity(), "新送达" + newImageList.size() + "个包裹", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loadMoreFooterView.setEnd(true);
                    Toast.makeText(getActivity(), "没有您的包裹了", Toast.LENGTH_SHORT).show();
                }
                searchImageListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(HttpRequest httpRequest, HttpResponse httpResponse, HttpRequest.Failure failure, boolean b, boolean b2) {
                if (getActivity() == null) {
                    return;
                }
                loadMoreFooterView.loadFinished(false);
                Toast.makeText(getActivity(), "快递投递失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCanceled(HttpRequest httpRequest) {
                loadMoreFooterView.loadFinished(false);
            }
        }).responseHandleCompletedAfterListener(new SearchImageRequest.ResponseHandler()).go();
    }
}
