package me.xiaopan.sketchsample.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.assemblyadapter.AssemblyRecyclerAdapter;
import me.xiaopan.assemblyadapter.OnRecyclerLoadMoreListener;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.activity.ApplyBackgroundCallback;
import me.xiaopan.sketchsample.activity.ImageDetailActivity;
import me.xiaopan.sketchsample.adapter.itemfactory.LoadMoreItemFactory;
import me.xiaopan.sketchsample.adapter.itemfactory.UnsplashPhotosItemFactory;
import me.xiaopan.sketchsample.bean.UnsplashImage;
import me.xiaopan.sketchsample.net.NetServices;
import me.xiaopan.sketchsample.widget.HintView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@InjectContentView(R.layout.fragment_recycler)
public class UnsplashPhotosFragment extends MyFragment implements UnsplashPhotosItemFactory.UnsplashPhotosItemEventListener, OnRecyclerLoadMoreListener {

    @InjectView(R.id.hint_recyclerFragment)
    private HintView hintView;

    @InjectView(R.id.recycler_recyclerFragment_content)
    private RecyclerView recyclerView;

    @InjectView(R.id.refresh_recyclerFragment)
    private SwipeRefreshLayout refreshLayout;

    private AssemblyRecyclerAdapter adapter;
    private int pageIndex = 1;

    private ApplyBackgroundCallback applyBackgroundCallback;
    private String backgroundImageUri;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof ApplyBackgroundCallback) {
            applyBackgroundCallback = (ApplyBackgroundCallback) getActivity();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(1);
            }
        });

        if (adapter != null) {
            recyclerView.setAdapter(adapter);
        } else {
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                }
            });
            loadData(1);
        }
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

    private void loadData(int pageIndex) {
        this.pageIndex = pageIndex;
        NetServices.unsplash().listPhotos(pageIndex).enqueue(new LoadDataCallback(this, pageIndex));
    }

    @Override
    public void onClickImage(int position, UnsplashImage image, String optionsKey) {
        // 含有这些信息时，说明这张图片不仅仅是缩小，而是会被改变，因此不能用作loading图了
        if (optionsKey.contains("Resize")
                || optionsKey.contains("ImageProcessor")
                || optionsKey.contains("thumbnailMode")) {
            optionsKey = null;
        }

        //noinspection unchecked
        List<UnsplashImage> images = adapter.getDataList();
        ArrayList<String> urlList = new ArrayList<String>(images.size());
        for (UnsplashImage unsplashImage : images) {
            urlList.add(unsplashImage.urls.regular);
        }

        ImageDetailActivity.launch(getActivity(), urlList, optionsKey, position);
    }

    @Override
    public void onClickUser(int position, UnsplashImage.User user) {
        String userHomeUri = user.links.html + "?utm_source=SketchSample&utm_medium=referral&utm_campaign=api-credit";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(userHomeUri));
        startActivity(intent);
    }

    @Override
    public void onLoadMore(AssemblyRecyclerAdapter assemblyRecyclerAdapter) {
        loadData(pageIndex + 1);
    }

    private static class LoadDataCallback implements Callback<List<UnsplashImage>> {
        private WeakReference<UnsplashPhotosFragment> reference;
        private int pageIndex;

        public LoadDataCallback(UnsplashPhotosFragment fragment, int pageIndex) {
            this.reference = new WeakReference<UnsplashPhotosFragment>(fragment);
            this.pageIndex = pageIndex;
        }

        @Override
        public void onResponse(Call<List<UnsplashImage>> call, Response<List<UnsplashImage>> response) {
            UnsplashPhotosFragment fragment = reference.get();
            if (fragment == null) {
                return;
            }

            if (pageIndex == 1) {
                create(fragment, response);
            } else {
                loadMore(fragment, response);
            }

            fragment.refreshLayout.setRefreshing(false);
        }

        private void create(UnsplashPhotosFragment fragment, Response<List<UnsplashImage>> response) {
            List<UnsplashImage> images = response.body();
            if (images == null || images.size() == 0) {
                fragment.hintView.empty("No photos");
                return;
            }

            AssemblyRecyclerAdapter adapter = new AssemblyRecyclerAdapter(images);
            adapter.addItemFactory(new UnsplashPhotosItemFactory(fragment));
            adapter.setLoadMoreItem(new LoadMoreItemFactory(fragment));

            fragment.recyclerView.setAdapter(adapter);
            fragment.adapter = adapter;

            fragment.changeBackground(images.get(0).urls.thumb);
        }

        private void loadMore(UnsplashPhotosFragment fragment, Response<List<UnsplashImage>> response) {
            List<UnsplashImage> images = response.body();
            if (images == null || images.size() == 0) {
                fragment.adapter.setLoadMoreEnd(true);
                return;
            }

            fragment.adapter.addAll(images);
            fragment.adapter.loadMoreFinished(images.size() < 20);

            fragment.changeBackground(images.get(0).urls.thumb);
        }

        @Override
        public void onFailure(Call<List<UnsplashImage>> call, Throwable t) {
            final UnsplashPhotosFragment fragment = reference.get();
            if (fragment == null) {
                return;
            }

            fragment.hintView.failed(t, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment.loadData(fragment.pageIndex);
                }
            });
        }
    }
}
