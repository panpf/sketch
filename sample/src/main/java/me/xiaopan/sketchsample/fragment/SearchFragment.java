package me.xiaopan.sketchsample.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import me.xiaopan.assemblyadapter.AssemblyRecyclerAdapter;
import me.xiaopan.assemblyadapter.OnRecyclerLoadMoreListener;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.BaseFragment;
import me.xiaopan.sketchsample.BindContentView;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.activity.ApplyBackgroundCallback;
import me.xiaopan.sketchsample.activity.ImageDetailActivity;
import me.xiaopan.sketchsample.adapter.itemfactory.LoadMoreItemFactory;
import me.xiaopan.sketchsample.adapter.itemfactory.StaggeredImageItemFactory;
import me.xiaopan.sketchsample.bean.BaiduImage;
import me.xiaopan.sketchsample.bean.BaiduImageSearchResult;
import me.xiaopan.sketchsample.bean.Image;
import me.xiaopan.sketchsample.net.NetServices;
import me.xiaopan.sketchsample.util.ScrollingPauseLoadManager;
import me.xiaopan.sketchsample.widget.HintView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 图片搜索Fragment
 */
@BindContentView(R.layout.fragment_recycler)
public class SearchFragment extends BaseFragment implements StaggeredImageItemFactory.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, OnRecyclerLoadMoreListener {
    public static final String PARAM_OPTIONAL_STRING_SEARCH_KEYWORD = "PARAM_OPTIONAL_STRING_SEARCH_KEYWORD";
    private static final int PAGE_SIZE = 60;

    @BindView(R.id.refresh_recyclerFragment)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.recycler_recyclerFragment_content)
    RecyclerView recyclerView;

    @BindView(R.id.hint_recyclerFragment)
    HintView hintView;

    private String searchKeyword = "GIF";

    private int pageIndex = 1;
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
        setHasOptionsMenu(true);

        Bundle arguments = getArguments();
        if (arguments != null) {
            searchKeyword = arguments.getString(PARAM_OPTIONAL_STRING_SEARCH_KEYWORD);
            if (searchKeyword == null) {
                searchKeyword = "GIF";
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setTitle(searchKeyword);
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
                        .hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

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

        refreshLayout.setOnRefreshListener(this);

        recyclerView.addOnScrollListener(new ScrollingPauseLoadManager(view.getContext()));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        int padding = SketchUtils.dp2px(getActivity(), 2);
        recyclerView.setPadding(padding, padding, padding, padding);
        recyclerView.setClipToPadding(false);

        if (adapter == null) {
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    onRefresh();
                }
            });
        } else {
            setAdapter(adapter);
        }
    }

    @Override
    public void onDestroyView() {
        setTitle("");
        super.onDestroyView();
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
        if (adapter != null) {
            adapter.setLoadMoreEnd(false);
        }

        if (!refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(true);
        }

        loadData(1);
    }

    @Override
    public void onItemClick(int position, BaiduImage image, String loadingImageOptionsInfo) {
        //noinspection unchecked
        List<BaiduImage> imageList = adapter.getDataList();
        ArrayList<Image> urlList = new ArrayList<>();
        for (BaiduImage imageItem : imageList) {
            urlList.add(new Image(imageItem.getSourceUrl(), imageItem.getSourceUrl()));
        }
        ImageDetailActivity.launch(getActivity(), urlList, loadingImageOptionsInfo, position - adapter.getHeaderItemCount());
    }

    private void loadData(int pageIndex) {
        this.pageIndex = pageIndex;
        int pageStart = (pageIndex - 1) * PAGE_SIZE;
        NetServices.baiduImage().searchPhoto(searchKeyword, searchKeyword, pageStart, PAGE_SIZE).enqueue(new LoadDataCallback(this, pageIndex));
    }

    @Override
    public void onLoadMore(AssemblyRecyclerAdapter assemblyRecyclerAdapter) {
        loadData(pageIndex + 1);
    }

    private static class LoadDataCallback implements Callback<BaiduImageSearchResult> {

        private WeakReference<SearchFragment> reference;
        private int pageIndex;

        LoadDataCallback(SearchFragment fragment, int pageIndex) {
            this.reference = new WeakReference<>(fragment);
            this.pageIndex = pageIndex;

            if (pageIndex == 1) {
                fragment.hintView.hidden();
            }
        }

        @Override
        public void onResponse(Call<BaiduImageSearchResult> call, Response<BaiduImageSearchResult> response) {
            SearchFragment fragment = reference.get();
            if (fragment == null) {
                return;
            }

            filterEmptyImage(response);

            if (pageIndex == 1) {
                create(fragment, response);
            } else {
                loadMore(fragment, response);
            }

            fragment.refreshLayout.setRefreshing(false);
        }

        private void filterEmptyImage(Response<BaiduImageSearchResult> response) {
            //noinspection ConstantConditions
            List<BaiduImage> imageList = response.body().getImageList();
            if (imageList != null) {
                Iterator<BaiduImage> imageIterator = imageList.iterator();
                while (imageIterator.hasNext()) {
                    BaiduImage image = imageIterator.next();
                    if (image.getSourceUrl() == null || "".equals(image.getSourceUrl())) {
                        imageIterator.remove();
                    }
                }
            }
        }

        @Override
        public void onFailure(Call<BaiduImageSearchResult> call, Throwable t) {
            final SearchFragment fragment = reference.get();
            if (fragment == null) {
                return;
            }

            fragment.hintView.failed(t, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment.onRefresh();
                }
            });

            fragment.refreshLayout.setRefreshing(false);
        }

        private void create(SearchFragment fragment, Response<BaiduImageSearchResult> response) {
            //noinspection ConstantConditions
            List<BaiduImage> images = response.body().getImageList();
            if (images == null || images.size() == 0) {
                fragment.hintView.empty("No photos");
                return;
            }

            AssemblyRecyclerAdapter adapter = new AssemblyRecyclerAdapter(images);
            adapter.addItemFactory(new StaggeredImageItemFactory(fragment));
            adapter.setLoadMoreItem(new LoadMoreItemFactory(fragment).fullSpan(fragment.recyclerView));

            fragment.recyclerView.setAdapter(adapter);
            fragment.adapter = adapter;

            fragment.changeBackground(images.get(0).getSourceUrl());
        }

        private void loadMore(SearchFragment fragment, Response<BaiduImageSearchResult> response) {
            //noinspection ConstantConditions
            List<BaiduImage> images = response.body().getImageList();
            if (images == null || images.size() == 0) {
                fragment.adapter.setLoadMoreEnd(true);
                return;
            }

            fragment.adapter.addAll(images);
            fragment.adapter.loadMoreFinished(images.size() < 20);

            fragment.changeBackground(images.get(0).getSourceUrl());
        }
    }
}
