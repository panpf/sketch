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

import butterknife.BindView;
import me.xiaopan.assemblyadapter.AssemblyRecyclerAdapter;
import me.xiaopan.assemblyadapter.OnRecyclerLoadMoreListener;
import me.xiaopan.sketchsample.BaseFragment;
import me.xiaopan.sketchsample.BindContentView;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.activity.ApplyBackgroundCallback;
import me.xiaopan.sketchsample.activity.ImageDetailActivity;
import me.xiaopan.sketchsample.adapter.itemfactory.LoadMoreItemFactory;
import me.xiaopan.sketchsample.adapter.itemfactory.UnsplashPhotosItemFactory;
import me.xiaopan.sketchsample.bean.Image;
import me.xiaopan.sketchsample.bean.UnsplashImage;
import me.xiaopan.sketchsample.net.NetServices;
import me.xiaopan.sketchsample.widget.HintView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@BindContentView(R.layout.fragment_recycler)
public class UnsplashPhotosFragment extends BaseFragment implements UnsplashPhotosItemFactory.UnsplashPhotosItemEventListener,
        OnRecyclerLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.hint_recyclerFragment)
    HintView hintView;

    @BindView(R.id.recycler_recyclerFragment_content)
    RecyclerView recyclerView;

    @BindView(R.id.refresh_recyclerFragment)
    SwipeRefreshLayout refreshLayout;

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

        refreshLayout.setOnRefreshListener(this);

        if (adapter != null) {
            recyclerView.setAdapter(adapter);
        } else {
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    onRefresh();
                }
            });
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
        ArrayList<Image> imageArrayList = new ArrayList<Image>(images.size());
        for (UnsplashImage unsplashImage : images) {
            imageArrayList.add(new Image(unsplashImage.urls.regular, unsplashImage.urls.raw));
        }

        ImageDetailActivity.launch(getActivity(), imageArrayList, optionsKey, position);
    }

    @Override
    public void onClickUser(int position, UnsplashImage.User user) {
        Uri uri = Uri.parse(user.links.html)
                .buildUpon()
                .appendQueryParameter("utm_source", "SketchSample")
                .appendQueryParameter("utm_medium", "referral")
                .appendQueryParameter("utm_campaign", "api-credit")
                .build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        startActivity(intent);
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
    public void onLoadMore(AssemblyRecyclerAdapter assemblyRecyclerAdapter) {
        loadData(pageIndex + 1);
    }

    private static class LoadDataCallback implements Callback<List<UnsplashImage>> {
        private WeakReference<UnsplashPhotosFragment> reference;
        private int pageIndex;

        LoadDataCallback(UnsplashPhotosFragment fragment, int pageIndex) {
            this.reference = new WeakReference<UnsplashPhotosFragment>(fragment);
            this.pageIndex = pageIndex;

            if (pageIndex == 1) {
                fragment.hintView.hidden();
            }
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

        @Override
        public void onFailure(Call<List<UnsplashImage>> call, Throwable t) {
            final UnsplashPhotosFragment fragment = reference.get();
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
    }
}
